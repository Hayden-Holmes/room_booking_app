package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.data.RoomRepository;
import com.room_booking_app.sprint1.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



class RoomSearchServiceTest {

    private RoomRepository roomRepository;
    private RoomSearchService roomSearchService;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        roomSearchService = new RoomSearchService(roomRepository);
    }

    @SuppressWarnings("unchecked")
    @Test
    void search_nullCriteria_callsFindAll_withConjunctionSpec_andReturnsRepoResult() {
        List<Room> expected = Collections.emptyList();
        when(roomRepository.findAll(anyRoomSpec())).thenReturn(expected);

        List<Room> result = roomSearchService.search(null);

        assertSame(expected, result);

        // verify it called findAll with a non-null spec
        @SuppressWarnings({"rawtypes"})
        ArgumentCaptor<Specification> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(roomRepository).findAll(specCaptor.capture());
        assertNotNull(specCaptor.getValue());
    }

    @Test
    void search_validCriteria_callsFindAll_andReturnsRepoResult() {
        RoomSearchCriteria criteria = new RoomSearchCriteria();
        criteria.setBuildingId(10L);
        criteria.setMinCapacity(4);
        criteria.setDate(LocalDate.of(2026, 3, 4));
        criteria.setStart("10:00");
        criteria.setEnd("11:00");

        List<Room> expected = List.of(new Room());
        when(roomRepository.findAll(anyRoomSpec())).thenReturn(expected);

        List<Room> result = roomSearchService.search(criteria);

        assertSame(expected, result);
        verify(roomRepository).findAll(anyRoomSpec());
    }

    @Test
    void search_invalidStartTime_throwsDateTimeParseException_andDoesNotCallRepo() {
        RoomSearchCriteria criteria = new RoomSearchCriteria();
        criteria.setDate(LocalDate.of(2026, 3, 4));
        criteria.setStart("not-a-time");
        criteria.setEnd("11:00");

        assertThrows(java.time.format.DateTimeParseException.class,
                () -> roomSearchService.search(criteria));

        verify(roomRepository, never()).findAll(anyRoomSpec());
    }

    @Test
    void search_missingTimePieces_doesNotParseTimes_stillCallsRepo() {
        RoomSearchCriteria criteria = new RoomSearchCriteria();
        criteria.setDate(LocalDate.of(2026, 3, 4));
        criteria.setStart(null);       // missing
        criteria.setEnd("11:00");

        when(roomRepository.findAll(anyRoomSpec())).thenReturn(Collections.emptyList());

        roomSearchService.search(criteria);

        verify(roomRepository).findAll(anyRoomSpec());
    }
    

    @SuppressWarnings("unchecked")
    private static Specification<Room> anyRoomSpec() {
        return (Specification<Room>) any(Specification.class);
    }
}