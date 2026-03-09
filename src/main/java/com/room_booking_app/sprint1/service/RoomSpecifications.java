package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.model.Amenity;
import com.room_booking_app.sprint1.model.Reservation;
import com.room_booking_app.sprint1.model.Room;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
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

    /**
     * Excludes rooms that have an overlapping BOOKED reservation in [start, end).
     * Overlap rule: res.start < end AND res.end > start
     */
    public static Specification<Room> isAvailable(LocalDateTime start, LocalDateTime end) {
        return (roomRoot, query, cb) -> {
            //throw exeption if only one of start/end is provided, otherwise ignore availability filter

            if (start == null || end == null) {
                if (start != null || end != null) {
                    throw new IllegalArgumentException("Both start and end must be provided together");
                }
                return cb.conjunction(); // ignore availability filter if not provided
            }

            // Subquery: find any overlapping reservation for this room
            var sub = query.subquery(Long.class); // subquery returns 1L if an overlapping reservation exists
            var res = sub.from(Reservation.class);// root of subquery is Reservation

            sub.select(cb.literal(1L))
               .where(
                   cb.equal(res.get("room"), roomRoot),
                   cb.equal(res.get("status"), Reservation.ReservationStatus.BOOKED),//!! grabbing enum from reservation model
                   cb.lessThan(res.get("startTime"), end),
                   cb.greaterThan(res.get("endTime"), start)
               );

            // NOT EXISTS (subquery)
            return cb.not(cb.exists(sub));
        };
    }
}