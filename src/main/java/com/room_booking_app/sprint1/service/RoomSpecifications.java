package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.model.Amenity;
import com.room_booking_app.sprint1.model.Reservation;
import com.room_booking_app.sprint1.model.Room;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;



public final class RoomSpecifications {

    private RoomSpecifications() { }

    //all match
    public static Specification<Room> hasAmenities(List<Long> amenityIds) {
    return (root, query, cb) -> {
        if (amenityIds == null || amenityIds.isEmpty()) {
            return cb.conjunction();
        }

        query.distinct(true);

        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Room> subRoot = subquery.from(Room.class);
        Join<Room, Amenity> subAmenityJoin = subRoot.join("amenities");

        subquery.select(subRoot.get("id"))
                .where(
                        cb.equal(subRoot.get("id"), root.get("id")),
                        subAmenityJoin.get("id").in(amenityIds)
                )
                .groupBy(subRoot.get("id"))
                .having(cb.equal(cb.countDistinct(subAmenityJoin.get("id")), amenityIds.size()));

        return cb.exists(subquery);
    };
}

    public static Specification<Room> hasBuildingId(Long buildingId) {
        return (root, query, cb) ->
                (buildingId == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("building").get("id"), buildingId);
    }

    public static Specification<Room> minCapacity(Integer minCapacity) {
        return (root, query, cb) ->
                (minCapacity == null) ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity);
    }

    
    public static Specification<Room> isAvailable(LocalDateTime start, LocalDateTime end) {
        return (roomRoot, query, cb) -> {
          

            if (start == null || end == null) {
                if (start != null || end != null) {
                    throw new IllegalArgumentException("Both start and end must be provided together");
                }
                return cb.conjunction(); 
            }

           
            var sub = query.subquery(Long.class); 
            var res = sub.from(Reservation.class);

            sub.select(cb.literal(1L))
               .where(
                   cb.equal(res.get("room"), roomRoot),
                   cb.equal(res.get("status"), Reservation.ReservationStatus.BOOKED),
                   cb.lessThan(res.get("startTime"), end),
                   cb.greaterThan(res.get("endTime"), start)
               );

            return cb.not(cb.exists(sub));
        };
    }
    public static Specification<Room> isWithinBuildingHours(LocalTime start, LocalTime end) {
        return (root, query, cb) -> cb.and(
            cb.lessThanOrEqualTo(root.get("building").get("openingTime"), start),
            cb.greaterThanOrEqualTo(root.get("building").get("closingTime"), end)

     );
}}