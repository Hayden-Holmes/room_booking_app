package com.room_booking_app.sprint1.controller;

import com.room_booking_app.sprint1.data.ReservationRepository;
import com.room_booking_app.sprint1.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DashboardControllerTest {

    private ReservationRepository reservationRepository;
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        dashboardController = new DashboardController(reservationRepository);
    }

    @Test
    void dashboard_returnsDashboardView_andAddsReservationsToModel() {
        Reservation reservation = new Reservation();
        when(reservationRepository.findByUserUsernameOrderByStartTimeDesc("hayden"))
                .thenReturn(List.of(reservation));

        Principal principal = () -> "hayden";
        Model model = new ConcurrentModel();

        String viewName = dashboardController.dashboard(principal, model);

        assertEquals("dashboard", viewName);
        assertTrue(model.containsAttribute("reservations"));
        assertEquals(List.of(reservation), model.getAttribute("reservations"));
        verify(reservationRepository).findByUserUsernameOrderByStartTimeDesc("hayden");
    }

    @Test
    void dashboard_redirectsToLogin_whenPrincipalIsNull() {
        Model model = new ConcurrentModel();

        String viewName = dashboardController.dashboard(null, model);

        assertEquals("redirect:/login", viewName);
        verify(reservationRepository, never()).findByUserUsernameOrderByStartTimeDesc(anyString());
    }
}