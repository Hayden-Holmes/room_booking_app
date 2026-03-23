package com.room_booking_app.sprint1.controller;

import com.room_booking_app.sprint1.data.AmenityRepository;
import com.room_booking_app.sprint1.data.BuildingRepository;
import com.room_booking_app.sprint1.model.Building;
import com.room_booking_app.sprint1.model.Room;
import com.room_booking_app.sprint1.service.RoomSearchCriteria;
import com.room_booking_app.sprint1.service.RoomSearchResult;
import com.room_booking_app.sprint1.service.RoomSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchControllerTest {

    private RoomSearchService roomSearchService;
    private BuildingRepository buildingRepository;
    private AmenityRepository amenityRepository;
    private SearchController searchController;

    @BeforeEach
    void setUp() {
        roomSearchService = mock(RoomSearchService.class);
        buildingRepository = mock(BuildingRepository.class);
        amenityRepository = mock(AmenityRepository.class);
        searchController = new SearchController(roomSearchService, buildingRepository, amenityRepository);
    }

    @Test
    void search_returnsSearchView_andAddsExpectedModelAttributes() {
        RoomSearchCriteria criteria = new RoomSearchCriteria();
        Model model = new ConcurrentModel();

        Room room1 = new Room();
        Room room2 = new Room();

        List<RoomSearchResult> rooms = List.of(
                new RoomSearchResult(room1, true, true),
                new RoomSearchResult(room2, false, true)
        );

        List<Building> buildings = List.of(new Building(), new Building());

        when(roomSearchService.search(criteria)).thenReturn(rooms);
        when(buildingRepository.findAll()).thenReturn(buildings);
        when(amenityRepository.findAll()).thenReturn(List.of());

        String viewName = searchController.search(criteria, model);

        assertEquals("search", viewName);
        assertSame(criteria, model.getAttribute("criteria"));
        assertEquals(rooms, model.getAttribute("rooms"));
        assertEquals(buildings, model.getAttribute("buildings"));
        assertEquals(List.of(), model.getAttribute("amenities"));
        assertTrue(model.containsAttribute("timeOptions"));

        @SuppressWarnings("unchecked")
        List<String> timeOptions = (List<String>) model.getAttribute("timeOptions");

        assertNotNull(timeOptions);
        assertEquals(96, timeOptions.size());
        assertEquals("00:00", timeOptions.get(0));
        assertEquals("00:15", timeOptions.get(1));
        assertEquals("23:45", timeOptions.get(timeOptions.size() - 1));

        verify(roomSearchService).search(criteria);
        verify(buildingRepository).findAll();
        verify(amenityRepository).findAll();
    }

    @Test
    void search_withNoResults_stillReturnsSearchView_andAddsEmptyRoomsList() {
        RoomSearchCriteria criteria = new RoomSearchCriteria();
        Model model = new ConcurrentModel();

        when(roomSearchService.search(criteria)).thenReturn(List.of());
        when(buildingRepository.findAll()).thenReturn(List.of());
        when(amenityRepository.findAll()).thenReturn(List.of());

        String viewName = searchController.search(criteria, model);

        assertEquals("search", viewName);
        assertEquals(List.of(), model.getAttribute("rooms"));
        assertEquals(List.of(), model.getAttribute("buildings"));
        assertEquals(List.of(), model.getAttribute("amenities"));

        @SuppressWarnings("unchecked")
        List<String> timeOptions = (List<String>) model.getAttribute("timeOptions");

        assertNotNull(timeOptions);
        assertEquals(96, timeOptions.size());

        verify(roomSearchService).search(criteria);
        verify(buildingRepository).findAll();
        verify(amenityRepository).findAll();
    }
}