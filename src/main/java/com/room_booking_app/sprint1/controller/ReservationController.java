package com.room_booking_app.sprint1.controller;

import com.room_booking_app.sprint1.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations/book")
    public String bookRoom(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();

        

        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = LocalDateTime.of(date, endTime);

        try {
            reservationService.book(roomId, start, end, username);

            redirectAttributes.addFlashAttribute("bookingSuccess", "Booking confirmed for " + date + " " + startTime + "–" + endTime);
        return "redirect:/reservations/confirmation";

        } catch (IllegalStateException | IllegalArgumentException ex) {
            // IllegalStateException: conflict / already booked
            // IllegalArgumentException: bad time window, etc.
            redirectAttributes.addFlashAttribute("bookingError", ex.getMessage());
            return "redirect:/reservations/failed";
        }

    }

    @PostMapping("/reservations/{id}/cancel")
    public String cancelReservation(
            @PathVariable("id") Long reservationId,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();

        try {
            reservationService.cancelReservation(reservationId, username);
            redirectAttributes.addFlashAttribute("cancelSuccess", "Reservation cancelled.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("cancelError", ex.getMessage());
        }

        
        return "redirect:/dashboard";
    }

    @GetMapping("/reservations/confirmation")
    public String bookingConfirmation() {
        return "booking-confirmation";
    }

    @GetMapping("/reservations/failed")
    public String bookingFailed() {
        return "booking-failed";
    }
}