package com.room_booking_app.sprint1.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.room_booking_app.sprint1.model.Reservation;
import com.room_booking_app.sprint1.data.ReservationRepository;

@Controller
public class DashboardController {

    private final ReservationRepository reservationRepository;

    public DashboardController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        List<Reservation> reservations =
            reservationRepository.findByUserUsernameOrderByStartTimeDesc(username);

        model.addAttribute("reservations", reservations);

        return "dashboard";
    }
}