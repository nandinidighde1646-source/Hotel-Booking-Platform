package com.hotel.hotelbooking.repository;

import com.hotel.hotelbooking.model.Booking;
import com.hotel.hotelbooking.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByGuestIdOrderByBookingDateDesc(Long guestId);

    List<Booking> findByStatus(BookingStatus status);

    long countByStatusIn(List<BookingStatus> statuses);

    /**
     * True if the given room already has a non-cancelled booking that
     * overlaps the requested date range. Used by BookingService before
     * confirming a new booking (prevents double booking, also covers
     * race conditions between the search and the actual booking call).
     */
    @Query("""
            SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
            FROM Booking b
            WHERE b.room.id = :roomId
              AND b.status <> com.hotel.hotelbooking.model.enums.BookingStatus.CANCELLED
              AND :checkIn < b.checkOutDate
              AND :checkOut > b.checkInDate
            """)
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                      @Param("checkIn") LocalDate checkIn,
                                      @Param("checkOut") LocalDate checkOut);

    @Query("SELECT b FROM Booking b WHERE b.checkInDate = :date AND b.status = com.hotel.hotelbooking.model.enums.BookingStatus.CONFIRMED")
    List<Booking> findTodaysCheckIns(@Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.checkOutDate = :date AND b.status = com.hotel.hotelbooking.model.enums.BookingStatus.CHECKED_IN")
    List<Booking> findTodaysCheckOuts(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.status IN :statuses")
    java.math.BigDecimal sumRevenueByStatuses(@Param("statuses") List<BookingStatus> statuses);

    List<Booking> findTop10ByOrderByBookingDateDesc();
}
