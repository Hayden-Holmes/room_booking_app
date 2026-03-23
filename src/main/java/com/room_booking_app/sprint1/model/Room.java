package com.room_booking_app.sprint1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "room")
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @ToString.Include
    private String name;

    private int capacity;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Building building;

    @ManyToMany
    @JoinTable(
            name = "room_amenity",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Amenity> amenities = new HashSet<>();

    @OneToMany(mappedBy = "room")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Reservation> reservations = new HashSet<>();

    public boolean isAvailableDuring(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return false;
        }

        if (reservations == null || reservations.isEmpty()) {
            return true;
        }

        for (Reservation reservation : new HashSet<>(reservations)) {
            if (start.isBefore(reservation.getEndTime()) &&
                end.isAfter(reservation.getStartTime())) {
                return false;
            }
        }

        return true;
    }

    public boolean isOpenDuring(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            return false;
        }
        if (building == null) {
            return false;
        }
        return building.isOpenDuring(start, end);
    }
}