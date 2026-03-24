package com.room_booking_app.sprint1.controller;

import com.room_booking_app.sprint1.data.ReservationRepository;
import com.room_booking_app.sprint1.data.RoomRepository;
import com.room_booking_app.sprint1.model.Reservation;
import com.room_booking_app.sprint1.model.Room;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
public class RoomController {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomController(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/rooms/{id}")
    public String roomDetails(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime end,
            Model model
    ) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + id));


        // Fetch reservations for the selected date (if provided)
        List<Reservation> reservationsForDay = List.of();
        Boolean buildingOpen = null;
        Boolean roomAvailable = null;

        if (date != null) {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            reservationsForDay = reservationRepository.findReservationsForDay(id, dayStart, dayEnd);
        }

        // Check building hours and room availability if date and time are provided
        if (date != null && start != null && end != null) {
            LocalDateTime startDateTime = LocalDateTime.of(date, start);
            LocalDateTime endDateTime = LocalDateTime.of(date, end);

            buildingOpen = room.isOpenDuring(start, end);
            roomAvailable = reservationRepository
                    .findOverlappingReservations(id, startDateTime, endDateTime)
                    .isEmpty();
        }

        model.addAttribute("room", room);
        model.addAttribute("date", date);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("reservationsForDay", reservationsForDay);
        model.addAttribute("buildingOpen", buildingOpen);
        model.addAttribute("roomAvailable", roomAvailable);

        return "room-details";
    }
}