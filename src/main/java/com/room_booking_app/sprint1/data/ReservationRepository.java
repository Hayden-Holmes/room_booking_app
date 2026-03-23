package com.room_booking_app.sprint1.data;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

        @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.room.id = :roomId
        AND r.startTime < :end
        AND r.endTime > :start
    """)
    List<Reservation> findOverlappingReservations(
            @Param("roomId") Long roomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.room.id = :roomId
        AND r.startTime >= :dayStart
        AND r.startTime < :dayEnd
        ORDER BY r.startTime
    """)
    List<Reservation> findReservationsForDay(
            @Param("roomId") Long roomId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd
    );
}
