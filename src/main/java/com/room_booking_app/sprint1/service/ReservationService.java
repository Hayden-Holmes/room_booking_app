package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.data.ReservationRepository;
import com.room_booking_app.sprint1.data.RoomRepository;
import com.room_booking_app.sprint1.data.UserRepository;
import com.room_booking_app.sprint1.model.Reservation;
import com.room_booking_app.sprint1.model.Room;
import com.room_booking_app.sprint1.model.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepo;
    private final RoomRepository roomRepo;
    private final UserRepository userRepo;

    public ReservationService(ReservationRepository reservationRepo,
                              RoomRepository roomRepo,
                              UserRepository userRepo) {
        this.reservationRepo = reservationRepo;
        this.roomRepo = roomRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public void book(Long roomId, LocalDateTime start, LocalDateTime end, String username) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end time are required.");
        }

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time cannot be in the past.");
        }

        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        User user = null;
        if (username != null) {
            user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        }

        boolean conflict = reservationRepo.existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                roomId, Reservation.ReservationStatus.BOOKED, end, start
        );

        if (conflict) {
            throw new IllegalStateException("Room already booked for that time range");
        }

        Reservation r = new Reservation();
        r.setRoom(room);
        r.setStartTime(start);
        r.setEndTime(end);
        r.setStatus(Reservation.ReservationStatus.BOOKED);
        if (user != null) {
            r.setUser(user);
        }

        reservationRepo.save(r);
    }

    @Transactional
    public void cancelReservation(Long reservationId, String currentUsername) {
        Reservation r = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

       
       if (r.getUser() == null || currentUsername == null ||!r.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Not allowed to cancel this reservation.");
        }

        r.setStatus(Reservation.ReservationStatus.CANCELLED);

        reservationRepo.save(r);
    }
}
