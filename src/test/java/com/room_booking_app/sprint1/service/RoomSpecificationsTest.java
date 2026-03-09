package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.model.Amenity;
import com.room_booking_app.sprint1.model.Building;
import com.room_booking_app.sprint1.model.Room;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.room_booking_app.sprint1.data.RoomRepository;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RoomSpecificationsTest {

    @Autowired
    private RoomRepository roomRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Amenity whiteboard;
    private Amenity tvScreen;
    private Amenity projector;
    private Building luddyHall;

    @BeforeEach
    void setUp() {
        String suffix = String.valueOf(System.currentTimeMillis());
        luddyHall = new Building();
        luddyHall.setName("Luddy Hall"+suffix);
        entityManager.persist(luddyHall);

        whiteboard = new Amenity();
        whiteboard.setName("Whiteboard"+suffix);
        entityManager.persist(whiteboard);

        tvScreen = new Amenity();
        tvScreen.setName("TV Screen"+suffix);
        entityManager.persist(tvScreen);

        projector= new Amenity();
        projector.setName("Projector"+suffix);
        entityManager.persist(projector);
    }

    @Test
    void hasAmenities_withNullAmenityIds_returnsAllRooms() {
        roomRepository.deleteAll();
        entityManager.flush();
        String suffix = String.valueOf(System.currentTimeMillis());
        Room room1 = makeRoom("LH-101"+suffix, 12, luddyHall, Set.of(whiteboard));
        Room room2 = makeRoom("LH-201"+suffix, 24, luddyHall, Set.of(tvScreen));

        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.flush();

        List<Room> results = roomRepository.findAll(RoomSpecifications.hasAmenities(null));

        assertEquals(2, results.size());
    }

    @Test
    void hasAmenities_withSingleAmenity_returnsRoomsContainingThatAmenity() {
        String suffix = String.valueOf(System.currentTimeMillis());
        Room room1 = makeRoom("LH-101"+suffix, 12, luddyHall, Set.of(whiteboard));
        Room room2 = makeRoom("LH-201"+suffix, 24, luddyHall, Set.of(tvScreen));

        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.flush();

        List<Room> results = roomRepository.findAll(
                RoomSpecifications.hasAmenities(List.of(whiteboard.getId()))
        );

        assertEquals(1, results.size());
        assertEquals("LH-101"+suffix, results.get(0).getName());
    }

    @Test
    void hasAmenities_withMultipleAmenities_currentlyMatchesAnySelectedAmenity_notAll() {
        String suffix = String.valueOf(System.currentTimeMillis());
        Room room1 = makeRoom("LH-101"+suffix, 12, luddyHall, Set.of(whiteboard,projector));
        Room room2 = makeRoom("LH-201"+suffix, 24, luddyHall, Set.of(whiteboard, tvScreen));

        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.flush();

        List<Room> results = roomRepository.findAll(
                RoomSpecifications.hasAmenities(List.of(whiteboard.getId(), tvScreen.getId()))
        );

        assertEquals(1, results.size());

        List<Room> tvScreenResults = roomRepository.findAll(
                RoomSpecifications.hasAmenities(List.of(tvScreen.getId(),whiteboard.getId()))
        );
        assertEquals(1, tvScreenResults.size());
        assertEquals("LH-201"+suffix, tvScreenResults.get(0).getName());
    }

    @Test
    void hasBuildingId_filtersByBuilding() {
        String suffix = String.valueOf(System.currentTimeMillis());
        Building wells = new Building();
        wells.setName("Wells Library"+suffix);
        entityManager.persist(wells);

        Room room1 = makeRoom("LH-101"+suffix, 12, luddyHall, Set.of(whiteboard));
        Room room2 = makeRoom("WL-B12"+suffix, 8, wells, Set.of(tvScreen));

        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.flush();

        List<Room> results = roomRepository.findAll(
                RoomSpecifications.hasBuildingId(luddyHall.getId())
        );

        assertEquals(1, results.size());
        assertEquals("LH-101"+suffix, results.get(0).getName());
    }

    @Test
    void minCapacity_filtersByMinimumCapacity() {
        roomRepository.deleteAll();
        entityManager.flush();
        String suffix = String.valueOf(System.currentTimeMillis());
        Room smallRoom = makeRoom("LH-101"+suffix, 12, luddyHall, Set.of(whiteboard));
        Room largeRoom = makeRoom("LH-201"+suffix, 24, luddyHall, Set.of(tvScreen));

        entityManager.persist(smallRoom);
        entityManager.persist(largeRoom);
        entityManager.flush();

        List<Room> results = roomRepository.findAll(
                RoomSpecifications.minCapacity(20)
        );

        assertEquals(1, results.size());
        assertEquals("LH-201"+suffix, results.get(0).getName());
    }

   

    private Room makeRoom(String name, int capacity, Building building, Set<Amenity> amenities) {
        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        room.setBuilding(building);
        room.setAmenities(amenities);
        return room;
    }
}