package com.room_booking_app.sprint1.data;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.room_booking_app.sprint1.model.Reservation;
import com.room_booking_app.sprint1.model.User;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findByUserOrderByStartTimeDesc(User user);
    List<Reservation> findByUserUsernameOrderByStartTimeDesc(String username);




    boolean existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
        Long roomId,
        Reservation.ReservationStatus status,
        LocalDateTime end,
        LocalDateTime start
    );
}
