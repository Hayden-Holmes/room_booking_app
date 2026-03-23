package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomSearchResult {
    private Room room;
    private boolean available;
    private boolean buildingOpen;

    public boolean isBookable() {
        return available && buildingOpen;
    }
}