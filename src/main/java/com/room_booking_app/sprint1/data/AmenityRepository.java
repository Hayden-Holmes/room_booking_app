package com.room_booking_app.sprint1.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import com.room_booking_app.sprint1.model.Amenity;

@Repository //extensions of @component, allows Spring to detect it during classpath scanning. Also indicates that this class is a repository.
public interface AmenityRepository extends CrudRepository<Amenity, Long> {
    Optional<Amenity> findByName(String name);
}