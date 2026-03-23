package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.data.ReservationRepository;
import com.room_booking_app.sprint1.data.RoomRepository;
import com.room_booking_app.sprint1.model.Building;
import com.room_booking_app.sprint1.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoomSearchServiceTest {

    private RoomRepository roomRepository;
    private ReservationRepository reservationRepository;
    private RoomSearchService roomSearchService;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        roomSearchService = new RoomSearchService(roomRepository, reservationRepository);
    }

    @SuppressWarnings("unchecked")
    @Test
    void search_nullCriteria_callsFindAll_withConjunctionSpec_andReturnsMappedResults() {
        Room room = new Room();
        List<Room> repoResult = List.of(room);

        when(roomRepository.findAll(anyRoomSpec())).thenReturn(repoResult);

        List<RoomSearchResult> result = roomSearchService.search(null);

        assertEquals(1, result.size());
        assertSame(room, result.get(0).getRoom());
        assertFalse(result.get(0).isAvailable());
        assertFalse(result.get(0).isBuildingOpen());
        assertFalse(result.get(0).isBookable());

        ArgumentCaptor<Specification> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(roomRepository).findAll(specCaptor.capture());
        assertNotNull(specCaptor.getValue());

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void search_validCriteria_callsFindAll_andReturnsMappedResults() {
        RoomSearchCriteria criteria = new RoomSearchCriteria();
        criteria.setBuildingId(10L);
        criteria.setMinCapacity(4);
        criteria.setDate(LocalDate.of(2026, 3, 4));
        criteria.setStart("10:00");
        criteria.setEnd("11:00");

        Building building = new Building();
        building.setOpeningTime(LocalTime.of(8, 0));
        building.setClosingTime(LocalTime.of(18, 0));

        Room room = new Room();
        room.setId(1L);
        room.setBuilding(building);

        when(roomRepository.findAll(anyRoomSpec())).thenReturn(List.of(room));
        when(reservationRepository.findOverlappingReservations(
                eq(1L),
                eq(LocalDateTime.of(2026, 3, 4, 10, 0)),
                eq(LocalDateTime.of(2026, 3, 4, 11, 0))
        )).thenReturn(List.of());

        List<RoomSearchResult> result = roomSearchService.search(criteria);

        assertEquals(1, result.size());
        assertSame(room, result.get(0).getRoom());
        assertTrue(result.get(0).isAvailable());
        assertTrue(result.get(0).isBuildingOpen());
        assertTrue(result.get(0).isBookable());

        verify(roomRepository).findAll(anyRoomSpec());
        verify(reservationRepository).findOverlappingReservations(
                eq(1L),
                eq(LocalDateTime.of(2026, 3, 4, 10, 0)),
                eq(LocalDateTime.of(2026, 3, 4, 11, 0))
        );
    }

    @Test
    void search_invalidStartTime_throwsDateTimeParseException_afterRepoFetch() {
        RoomSearchCriteria criteria = new RoomSearchCriteria();
        criteria.setDate(LocalDate.of(2026, 3, 4));
        criteria.setStart("not-a-time");
        criteria.setEnd("11:00");

        when(roomRepository.findAll(anyRoomSpec())).thenReturn(Collections.emptyList());

        assertThrows(java.time.format.DateTimeParseException.class,
                () -> roomSearchService.search(criteria));

        verify(roomRepository).findAll(anyRoomSpec());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void search_missingTimePieces_doesNotParseTimes_stillCallsRepo() {
        RoomSearchCriteria criteria = new RoomSearchCriteria();
        criteria.setDate(LocalDate.of(2026, 3, 4));
        criteria.setStart(null);
        criteria.setEnd("11:00");

        when(roomRepository.findAll(anyRoomSpec())).thenReturn(Collections.emptyList());

        List<RoomSearchResult> result = roomSearchService.search(criteria);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(roomRepository).findAll(anyRoomSpec());
        verifyNoInteractions(reservationRepository);
    }

    @SuppressWarnings("unchecked")
    private static Specification<Room> anyRoomSpec() {
        return (Specification<Room>) any(Specification.class);
    }
}