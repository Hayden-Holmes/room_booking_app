package com.room_booking_app.sprint1.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.room_booking_app.sprint1.data.RoomRepository;
import com.room_booking_app.sprint1.model.Room;


@Service
public class RoomSearchService {

    private final RoomRepository roomRepository;

    public RoomSearchService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> search(RoomSearchCriteria criteria) {

       Specification<Room> spec = (root, query, cb) -> cb.conjunction();

        if (criteria == null) {
            return roomRepository.findAll(spec);
        }

        if (criteria.getBuildingId() != null) {
            spec = spec.and(RoomSpecifications.hasBuildingId(criteria.getBuildingId()));
        }

        if (criteria.getMinCapacity() != null) {
            spec = spec.and(RoomSpecifications.minCapacity(criteria.getMinCapacity()));
        }


        //is available
      
        if(criteria.getDate() != null && criteria.getStart() != null && criteria.getEnd() != null) {
            LocalDate d = criteria.getDate();
            LocalTime s = LocalTime.parse(criteria.getStart()); // expects "HH:mm"
            LocalTime e = LocalTime.parse(criteria.getEnd());

            LocalDateTime start = LocalDateTime.of(d,s);
            LocalDateTime end = LocalDateTime.of(d,e);

            spec = spec.and(RoomSpecifications.isAvailable(start, end));
        }

        //amenities
        if (criteria.getRequiredAmenities() != null && !criteria.getRequiredAmenities().isEmpty()) {
            List<Long> amenityIds = criteria.getRequiredAmenities().stream()
                    .map(a -> a.getId())
                    .toList();
            spec = spec.and(RoomSpecifications.hasAmenities(amenityIds));
        }

        return roomRepository.findAll(spec);

    }
}