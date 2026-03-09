package com.room_booking_app.sprint1.controller;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.room_booking_app.sprint1.model.Room;
import com.room_booking_app.sprint1.service.RoomSearchCriteria;
import com.room_booking_app.sprint1.service.RoomSearchService;
import com.room_booking_app.sprint1.data.BuildingRepository;
import com.room_booking_app.sprint1.data.AmenityRepository;


@Controller
public class SearchController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SearchController.class);

    private final RoomSearchService roomSearchService;
    private final BuildingRepository buildingRepository;
    private final AmenityRepository amenityRepository;

    public SearchController(RoomSearchService roomSearchService, BuildingRepository buildingRepository, AmenityRepository amenityRepository) {
        this.roomSearchService = roomSearchService;
        this.buildingRepository = buildingRepository;
        this.amenityRepository = amenityRepository;
    }

    @GetMapping("/search")
    public String search(@ModelAttribute("criteria") RoomSearchCriteria criteria, Model model) {
        List<Room> rooms = roomSearchService.search(criteria);
        model.addAttribute("criteria", criteria);
        model.addAttribute("rooms", rooms);
        model.addAttribute("buildings", buildingRepository.findAll());
        model.addAttribute("amenities", amenityRepository.findAll());
        model.addAttribute("timeOptions", generateQuarterHours());
        log.info("Search performed with criteria: {}", criteria);
        Iterable<?> amenities = amenityRepository.findAll();
        System.out.println("Amenities: " + amenities);
        System.out.println("Amenities: " + amenityRepository.findAll());
        return "search";
    }

    private List<String> generateQuarterHours() {
        List<String> timeOptions = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                timeOptions.add(String.format("%02d:%02d", hour, minute));
            }
        }
        return timeOptions;
    }
}