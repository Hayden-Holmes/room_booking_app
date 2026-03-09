package com.room_booking_app.sprint1.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import com.room_booking_app.sprint1.model.Building;

@Repository
public interface BuildingRepository extends CrudRepository<Building, Long> {
    Optional<Building> findByName(String name);
}