package com.room_booking_app.sprint1.model;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="building")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private LocalTime openingTime;
    private LocalTime closingTime;

    public boolean isOpenDuring(LocalTime start, LocalTime end) {
        if (openingTime == null || closingTime == null || start == null || end == null) {
            return false;
        }
        return !start.isBefore(openingTime) && !end.isAfter(closingTime);
}
}

