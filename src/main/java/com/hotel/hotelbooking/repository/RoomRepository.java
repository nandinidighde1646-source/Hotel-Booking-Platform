package com.hotel.hotelbooking.repository;

import com.hotel.hotelbooking.model.Room;
import com.hotel.hotelbooking.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNumber(String roomNumber);

    long countByStatus(com.hotel.hotelbooking.model.enums.RoomStatus status);

    /**
     * Finds every active room whose type can hold at least `guests` people
     * AND that has NO overlapping, non-cancelled booking for the requested
     * date range. This is the core "prevent double booking" query.
     *
     * Two date ranges overlap when:
     *   requestedCheckIn < existingCheckOut  AND  requestedCheckOut > existingCheckIn
     * So a room is EXCLUDED if such a booking exists.
     */
    @Query("""
            SELECT r FROM Room r
            WHERE r.active = true
              AND r.roomType.capacity >= :guests
              AND r.id NOT IN (
                    SELECT b.room.id FROM Booking b
                    WHERE b.status <> com.hotel.hotelbooking.model.enums.BookingStatus.CANCELLED
                      AND :checkIn < b.checkOutDate
                      AND :checkOut > b.checkInDate
              )
            """)
    List<Room> findAvailableRooms(@Param("checkIn") LocalDate checkIn,
                                   @Param("checkOut") LocalDate checkOut,
                                   @Param("guests") int guests);
}
