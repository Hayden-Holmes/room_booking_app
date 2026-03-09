package com.room_booking_app.sprint1.controller;

import com.room_booking_app.sprint1.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

    private ReservationService reservationService;
    private ReservationController reservationController;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        reservationService = mock(ReservationService.class);
        reservationController = new ReservationController(reservationService);

        userDetails = User.withUsername("hayden")
                .password("password")
                .roles("USER")
                .build();
    }

    @Test
    void bookRoom_success_redirectsToConfirmation_andSetsSuccessFlashAttribute() {
        Long roomId = 1L;
        LocalDate date = LocalDate.of(2026, 3, 9);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = LocalDateTime.of(date, endTime);

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = reservationController.bookRoom(
                roomId,
                date,
                startTime,
                endTime,
                redirectAttributes,
                userDetails
        );

        assertEquals("redirect:/reservations/confirmation", viewName);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("bookingSuccess"));
        verify(reservationService).book(roomId, start, end, "hayden");
    }

    @Test
    void bookRoom_whenServiceThrowsIllegalStateException_redirectsToFailed_andSetsErrorFlashAttribute() {
        Long roomId = 1L;
        LocalDate date = LocalDate.of(2026, 3, 9);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = LocalDateTime.of(date, endTime);

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        doThrow(new IllegalStateException("Room already booked"))
                .when(reservationService).book(roomId, start, end, "hayden");

        String viewName = reservationController.bookRoom(
                roomId,
                date,
                startTime,
                endTime,
                redirectAttributes,
                userDetails
        );

        assertEquals("redirect:/reservations/failed", viewName);
        assertEquals("Room already booked", redirectAttributes.getFlashAttributes().get("bookingError"));
        verify(reservationService).book(roomId, start, end, "hayden");
    }

    @Test
    void bookRoom_whenServiceThrowsIllegalArgumentException_redirectsToFailed_andSetsErrorFlashAttribute() {
        Long roomId = 1L;
        LocalDate date = LocalDate.of(2026, 3, 9);
        LocalTime startTime = LocalTime.of(11, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = LocalDateTime.of(date, endTime);

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        doThrow(new IllegalArgumentException("End time must be after start time"))
                .when(reservationService).book(roomId, start, end, "hayden");

        String viewName = reservationController.bookRoom(
                roomId,
                date,
                startTime,
                endTime,
                redirectAttributes,
                userDetails
        );

        assertEquals("redirect:/reservations/failed", viewName);
        assertEquals("End time must be after start time", redirectAttributes.getFlashAttributes().get("bookingError"));
        verify(reservationService).book(roomId, start, end, "hayden");
    }

    @Test
    void cancelReservation_success_redirectsToSearch_andSetsSuccessFlashAttribute() {
        Long reservationId = 5L;
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = reservationController.cancelReservation(
                reservationId,
                redirectAttributes,
                userDetails
        );

        assertEquals("redirect:/dashboard", viewName);
        assertEquals("Reservation cancelled.", redirectAttributes.getFlashAttributes().get("cancelSuccess"));
        verify(reservationService).cancelReservation(reservationId, "hayden");
    }

    @Test
    void cancelReservation_whenServiceThrowsException_redirectsToSearch_andSetsErrorFlashAttribute() {
        Long reservationId = 5L;
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        doThrow(new RuntimeException("Not authorized to cancel this reservation"))
                .when(reservationService).cancelReservation(reservationId, "hayden");

        String viewName = reservationController.cancelReservation(
                reservationId,
                redirectAttributes,
                userDetails
        );

        assertEquals("redirect:/dashboard", viewName);
        assertEquals(
                "Not authorized to cancel this reservation",
                redirectAttributes.getFlashAttributes().get("cancelError")
        );
        verify(reservationService).cancelReservation(reservationId, "hayden");
    }

    @Test
    void bookingConfirmation_returnsConfirmationView() {
        assertEquals("booking-confirmation", reservationController.bookingConfirmation());
    }

    @Test
    void bookingFailed_returnsFailedView() {
        assertEquals("booking-failed", reservationController.bookingFailed());
    }
}