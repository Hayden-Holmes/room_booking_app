package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.model.Amenity;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class RoomSearchCriteria {
    private Long buildingId;                   // optional
    private Integer minCapacity;               // optional
    private Set<Amenity> requiredAmenities;    // optional (empty = ignore)
    private LocalDate date;               // not optional
    private String start;              // not optional
    private String end;                 // not ooptional
}