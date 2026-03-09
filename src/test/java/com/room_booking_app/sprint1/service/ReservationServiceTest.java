package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.data.ReservationRepository;
import com.room_booking_app.sprint1.data.RoomRepository;
import com.room_booking_app.sprint1.data.UserRepository;
import com.room_booking_app.sprint1.model.Reservation;
import com.room_booking_app.sprint1.model.Room;
import com.room_booking_app.sprint1.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationRepository reservationRepo;
    private RoomRepository roomRepo;
    private UserRepository userRepo;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationRepo = mock(ReservationRepository.class);
        roomRepo = mock(RoomRepository.class);
        userRepo = mock(UserRepository.class);

        reservationService = new ReservationService(reservationRepo, roomRepo, userRepo);
    }

    // -----------------------
    // book() tests
    // -----------------------

    @Test
    void book_startNull_throwsIllegalArgumentException() {
        Long roomId = 1L;
        LocalDateTime start = null;
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.book(roomId, start, end, "user"));

        verifyNoInteractions(roomRepo, userRepo, reservationRepo);
    }

    @Test
    void book_endNotAfterStart_throwsIllegalArgumentException() {
        Long roomId = 1L;
        LocalDateTime start = LocalDateTime.of(2026, 3, 4, 10, 0);
        LocalDateTime end = start; // not after

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.book(roomId, start, end, "user"));

        verifyNoInteractions(roomRepo, userRepo, reservationRepo);
    }

    

    @Test
    void book_conflictExists_throwsIllegalStateException_andDoesNotSave() {
        Long roomId = 1L;
        LocalDateTime start = LocalDateTime.of(2026, 3, 4, 10, 0);
        LocalDateTime end = start.plusHours(1);
        String username = "hayden";

        when(roomRepo.findById(roomId)).thenReturn(Optional.of(new Room()));
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(reservationRepo.existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                roomId, Reservation.ReservationStatus.BOOKED, end, start
        )).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.book(roomId, start, end, username));

        verify(reservationRepo, never()).save(any());
    }

    @Test
    void book_noConflict_savesReservation_withBookedStatus_andTimes_andRoom_andUser() {
        Long roomId = 1L;
        LocalDateTime start = LocalDateTime.of(2027, 3, 4, 10, 0);
        LocalDateTime end = start.plusHours(1);
        String username = "hayden";

        Room room = new Room();
        User user = new User();


        when(roomRepo.findById(roomId)).thenReturn(Optional.of(room));
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(reservationRepo.existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                roomId, Reservation.ReservationStatus.BOOKED, end, start
        )).thenReturn(false);

        reservationService.book(roomId, start, end, username);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepo).save(captor.capture());

        Reservation saved = captor.getValue();
        assertNotNull(saved);
        assertEquals(Reservation.ReservationStatus.BOOKED, saved.getStatus());
        assertEquals(start, saved.getStartTime());
        assertEquals(end, saved.getEndTime());
        assertEquals(room, saved.getRoom());
        assertEquals(user, saved.getUser());
    }

    @Test
    void book_nullUsername_allowed_savesReservation_withoutUser() {
        Long roomId = 1L;
        LocalDateTime start = LocalDateTime.of(2027, 3, 4, 10, 0);
        LocalDateTime end = start.plusHours(1);

        Room room = new Room();

        when(roomRepo.findById(roomId)).thenReturn(Optional.of(room));
        when(reservationRepo.existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                roomId, Reservation.ReservationStatus.BOOKED, end, start
        )).thenReturn(false);

        reservationService.book(roomId, start, end, null);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepo).save(captor.capture());

        Reservation saved = captor.getValue();
        assertEquals(room, saved.getRoom());
        assertNull(saved.getUser());
        assertEquals(Reservation.ReservationStatus.BOOKED, saved.getStatus());
    }

    // -----------------------
    // cancelReservation() tests
    // -----------------------

    @Test
    void cancelReservation_notFound_throwsIllegalArgumentException() {
        Long reservationId = 55L;

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.cancelReservation(reservationId, "hayden"));

        verify(reservationRepo).findById(reservationId);
        verify(reservationRepo, never()).save(any());
    }

    @Test
    void cancelReservation_reservationHasNoUser_throwsAccessDenied() {
        Long reservationId = 1L;
        Reservation r = new Reservation();
        r.setUser(null);

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(r));

        assertThrows(AccessDeniedException.class,
                () -> reservationService.cancelReservation(reservationId, "hayden"));

        verify(reservationRepo, never()).save(any());
    }

    @Test
    void cancelReservation_currentUsernameNull_throwsAccessDenied() {
        Long reservationId = 1L;

        User owner = new User();
        owner.setUsername("hayden");

        Reservation r = new Reservation();
        r.setUser(owner);

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(r));

        assertThrows(AccessDeniedException.class,
                () -> reservationService.cancelReservation(reservationId, null));

        verify(reservationRepo, never()).save(any());
    }

    @Test
    void cancelReservation_usernameMismatch_throwsAccessDenied() {
        Long reservationId = 1L;

        User owner = new User();
        owner.setUsername("hayden");

        Reservation r = new Reservation();
        r.setUser(owner);

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(r));

        assertThrows(AccessDeniedException.class,
                () -> reservationService.cancelReservation(reservationId, "someoneElse"));

        verify(reservationRepo, never()).save(any());
    }

    @Test
    void cancelReservation_ownerMatches_setsCancelled_andSaves() {
        Long reservationId = 1L;

        User owner = new User();
        owner.setUsername("hayden");

        Reservation r = new Reservation();
        r.setUser(owner);
        r.setStatus(Reservation.ReservationStatus.BOOKED);

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(r));

        reservationService.cancelReservation(reservationId, "hayden");

        assertEquals(Reservation.ReservationStatus.CANCELLED, r.getStatus());
        verify(reservationRepo).save(r);
    }
}