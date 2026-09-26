package com.hotel.hotelbooking.service;

import com.hotel.hotelbooking.exception.BookingException;
import com.hotel.hotelbooking.model.Booking;
import com.hotel.hotelbooking.model.CheckIn;
import com.hotel.hotelbooking.model.Room;
import com.hotel.hotelbooking.model.User;
import com.hotel.hotelbooking.model.enums.BookingStatus;
import com.hotel.hotelbooking.model.enums.RoomStatus;
import com.hotel.hotelbooking.repository.CheckInRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final BookingService bookingService;

    public CheckInService(CheckInRepository checkInRepository, BookingService bookingService) {
        this.checkInRepository = checkInRepository;
        this.bookingService = bookingService;
    }

    public List<Booking> todaysCheckIns() {
        return bookingService.findTodaysCheckIns();
    }

    /**
     * Performs check-in: Booking -> CHECKED_IN, Room -> OCCUPIED.
     * Also records who (staff) did it and when.
     */
    @Transactional
    public CheckIn checkIn(Long bookingId, User staff, String remarks) {
        Booking booking = bookingService.getById(bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingException("Only a CONFIRMED booking can be checked in.");
        }
        if (booking.getCheckInDate().isAfter(LocalDate.now())) {
            throw new BookingException("This booking's check-in date has not arrived yet.");
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.OCCUPIED);
        bookingService.save(booking);

        CheckIn checkIn = new CheckIn();
        checkIn.setBooking(booking);
        checkIn.setStaff(staff);
        checkIn.setRemarks(remarks);
        return checkInRepository.save(checkIn);
    }
}
