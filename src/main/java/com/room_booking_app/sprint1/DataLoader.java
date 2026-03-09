package com.room_booking_app.sprint1;

import com.room_booking_app.sprint1.model.*;
import com.room_booking_app.sprint1.data.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final BuildingRepository buildingRepo;
    private final AmenityRepository amenityRepo;
    private final RoomRepository roomRepo;
    private final ReservationRepository reservationRepo;
    private final UserRepository userRepo;

    public DataLoader(BuildingRepository buildingRepo,
                      AmenityRepository amenityRepo,
                      RoomRepository roomRepo,
                      ReservationRepository reservationRepo,
                      UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.buildingRepo = buildingRepo;
        this.amenityRepo = amenityRepo;
        this.roomRepo = roomRepo;
        this.reservationRepo = reservationRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // prevent duplicate seeding
        if (buildingRepo.count() > 0) {
            System.out.println("Seed skipped.");
            return;
        }  

        loadUsers();
        loadBuildings();
        loadAmenities();
        loadRooms();
        loadRoomAmenities();
        loadReservations();

        System.out.println("Seed complete.");
    }

    //// ----------------------------
    /// LOAD USERS
    /// ----------------------------
    private void loadUsers() throws Exception {
        var br = reader("seed/users.csv");
        br.readLine(); // skip header
        String line;

        while ((line = br.readLine()) != null) {
            String[] cols = line.split(",");

            User user = new User();
            user.setUsername(cols[0].trim());
            user.setPassword(passwordEncoder.encode(cols[1].trim()));
            user.setEmail(cols[2].trim());
            user.getRoles().add((cols[3].trim()));

            userRepo.save(user);
        }
        userRepo.findAll().forEach(System.out::println);
    }

    // ----------------------------
    // LOAD BUILDINGS
    // ----------------------------
    private void loadBuildings() throws Exception {
        var br = reader("seed/buildings.csv");

        br.readLine(); // skip header
        String line;

        while ((line = br.readLine()) != null) {
            String[] cols = line.split(",");

            Building b = new Building();
            b.setName(cols[0].trim());
            b.setAddress(cols[1].trim());

            buildingRepo.save(b);
        }
    }

    // ----------------------------
    // LOAD AMENITIES
    // ----------------------------
    private void loadAmenities() throws Exception {
        var br = reader("seed/amenities.csv");

        br.readLine();
        String line;

        while ((line = br.readLine()) != null) {
            Amenity a = new Amenity();
            a.setName(line.trim());

            amenityRepo.save(a);
        }
    }

    // ----------------------------
    // LOAD ROOMS
    // ----------------------------
    private void loadRooms() throws Exception {
        var br = reader("seed/rooms.csv");

        br.readLine();
        String line;
    

        while ((line = br.readLine()) != null) {
            String[] cols = line.split(",");

            String roomName = cols[0].trim();
            int capacity = Integer.parseInt(cols[1].trim());
            String buildingName = cols[2].trim();

            Building building = buildingRepo.findByName(buildingName)
                    .orElseThrow(() -> new RuntimeException("Unknown building: " + buildingName));

            Room room = new Room();
            room.setName(roomName);
            room.setCapacity(capacity);
            room.setBuilding(building);

            roomRepo.save(room);
        }
    }

    // ----------------------------
    // LOAD ROOM-AMENITY (MANY-TO-MANY)
    // ----------------------------
    private void loadRoomAmenities() throws Exception {
        var br = reader("seed/room_amenities.csv");

        br.readLine();
        String line;

        while ((line = br.readLine()) != null) {
            String[] cols = line.split(",");

            String roomName = cols[0].trim();
            String amenityName = cols[1].trim();

            Room room = roomRepo.findByName(roomName)
                    .orElseThrow(() -> new RuntimeException("Unknown room: " + roomName));

            Amenity amenity = amenityRepo.findByName(amenityName)
                    .orElseThrow(() -> new RuntimeException("Unknown amenity: " + amenityName));

            room.getAmenities().add(amenity);
            roomRepo.save(room); // persist join row
        }
    }

    // ----------------------------
    // LOAD RESERVATIONS
    // ----------------------------
    private void loadReservations() throws Exception {
        var br = reader("seed/reservations.csv");

        br.readLine();
        String line;

        while ((line = br.readLine()) != null) {
            String[] cols = line.split(",");
            String roomName = cols[0].trim();
            String username = cols[1].trim();
            LocalDateTime start = LocalDateTime.parse(cols[2].trim());
            LocalDateTime end = LocalDateTime.parse(cols[3].trim());
            Reservation.ReservationStatus status =
                    Reservation.ReservationStatus.valueOf(cols[4].trim());

            Room room = roomRepo.findByName(roomName)
                    .orElseThrow(() -> new RuntimeException("Unknown room: " + roomName));

            Reservation r = new Reservation();
            r.setRoom(room);
            r.setUser(userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Unknown user: " + username)));
            r.setStartTime(start);
            r.setEndTime(end);
            r.setStatus(status);

            reservationRepo.save(r);
        }
    }

    // ----------------------------
    // HELPERs
    // ----------------------------
    private BufferedReader reader(String path) throws Exception {
        return new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource(path).getInputStream(),
                        StandardCharsets.UTF_8
                )
        );
    }

}