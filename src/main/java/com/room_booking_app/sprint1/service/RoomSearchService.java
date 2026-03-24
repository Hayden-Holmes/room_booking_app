package com.room_booking_app.sprint1.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.room_booking_app.sprint1.data.ReservationRepository;
import com.room_booking_app.sprint1.data.RoomRepository;
import com.room_booking_app.sprint1.model.Room;

@Service
public class RoomSearchService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomSearchService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<RoomSearchResult> search(RoomSearchCriteria criteria) {

        Specification<Room> spec = (root, query, cb) -> cb.conjunction();

        if (criteria == null) {
            return roomRepository.findAll(spec).stream()
                    .map(room -> new RoomSearchResult(room, false, false))
                    .toList();
        }

        if (criteria.getBuildingId() != null) {
            spec = spec.and(RoomSpecifications.hasBuildingId(criteria.getBuildingId()));
        }

        if (criteria.getMinCapacity() != null) {
            spec = spec.and(RoomSpecifications.minCapacity(criteria.getMinCapacity()));
        }

        if (criteria.getRequiredAmenities() != null && !criteria.getRequiredAmenities().isEmpty()) {
            List<Long> amenityIds = criteria.getRequiredAmenities().stream()
                    .map(a -> a.getId())
                    .toList();
            spec = spec.and(RoomSpecifications.hasAmenities(amenityIds));
        }

        List<Room> rooms = roomRepository.findAll(spec);

        List<RoomSearchResult> results;

        if (criteria.isHasTime()) {
            LocalDate d = criteria.getDate();
            LocalTime s = LocalTime.parse(criteria.getStart());
            LocalTime e = LocalTime.parse(criteria.getEnd());

            LocalDateTime start = LocalDateTime.of(d, s);
            LocalDateTime end = LocalDateTime.of(d, e);

                results = rooms.stream()
                    .map(room -> {
                        boolean available = reservationRepository
                                .findOverlappingReservations(room.getId(), start, end)
                                .isEmpty();

                        boolean buildingOpen = room.getBuilding() != null
                                && room.getBuilding().isOpenDuring(s, e);

                        return new RoomSearchResult(room, available, buildingOpen);
                    })
                    .toList();
        }

        // If no date/time criteria, just return rooms without availability info
       else{  
            results =  rooms.stream()
            .map(room -> new RoomSearchResult(room, false, false))
            .toList();
       }

       

    return results;
    }
        
    }
    
