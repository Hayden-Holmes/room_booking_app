package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.model.Amenity;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class RoomSearchCriteria {
    private Long buildingId;                  
    private Integer minCapacity;               
    private Set<Amenity> requiredAmenities;    
    private LocalDate date;               
    private String start;              
    private String end; 
    public boolean isHasTime() {
        return date != null && start != null && end != null;
}                
}