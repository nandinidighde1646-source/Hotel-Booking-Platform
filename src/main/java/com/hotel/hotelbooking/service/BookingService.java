package com.hotel.hotelbooking.service;

import com.hotel.hotelbooking.exception.BookingException;
import com.hotel.hotelbooking.exception.ResourceNotFoundException;
import com.hotel.hotelbooking.model.Booking;
import com.hotel.hotelbooking.model.Guest;
import com.hotel.hotelbooking.model.Room;
import com.hotel.hotelbooking.model.enums.BookingStatus;
import com.hotel.hotelbooking.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    public BookingService(BookingRepository bookingRepository, RoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
    }

    /**
     * Creates a new booking for a guest.
     * Re-validates dates and re-checks overlap at the DB level right
     * before saving, so two guests cannot double-book the same room
     * even if both searched availability at the same time.
     */
    @Transactional
    public Booking createBooking(Guest guest, Long roomId, LocalDate checkIn, LocalDate checkOut) {
        roomService.validateDateRange(checkIn, checkOut);

        Room room = roomService.getById(roomId);
        if (!room.isActive()) {
            throw new BookingException("This room is not available for booking.");
        }

        if (bookingRepository.existsOverlappingBooking(roomId, checkIn, checkOut)) {
            throw new BookingException("Sorry, this room was just booked for the selected dates. Please choose another room or dates.");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal totalAmount = room.getRoomType().getBasePrice().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }

    public List<Booking> getHistoryForGuest(Long guestId) {
        return bookingRepository.findByGuestIdOrderByBookingDateDesc(guestId);
    }

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking cancel(Long bookingId, Long requestingGuestId, boolean isStaffOrAdmin) {
        Booking booking = getById(bookingId);

        if (!isStaffOrAdmin && !booking.getGuest().getId().equals(requestingGuestId)) {
            throw new BookingException("You can only cancel your own bookings.");
        }
        if (booking.getStatus() == BookingStatus.CHECKED_IN || booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new BookingException("A booking that has already been checked in/out cannot be cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public long countActiveBookings() {
        return bookingRepository.countByStatusIn(List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN));
    }

    public List<Booking> findTodaysCheckIns() {
        return bookingRepository.findTodaysCheckIns(LocalDate.now());
    }

    public List<Booking> findTodaysCheckOuts() {
        return bookingRepository.findTodaysCheckOuts(LocalDate.now());
    }

    public BigDecimal totalRevenue() {
        return bookingRepository.sumRevenueByStatuses(List.of(BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN, BookingStatus.CHECKED_OUT));
    }

    public List<Booking> recentBookings() {
        return bookingRepository.findTop10ByOrderByBookingDateDesc();
    }

    @Transactional
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }
}
