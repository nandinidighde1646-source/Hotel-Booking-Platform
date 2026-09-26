package com.hotel.hotelbooking.service;

import com.hotel.hotelbooking.exception.BookingException;
import com.hotel.hotelbooking.model.Booking;
import com.hotel.hotelbooking.model.CheckOut;
import com.hotel.hotelbooking.model.Room;
import com.hotel.hotelbooking.model.User;
import com.hotel.hotelbooking.model.enums.BookingStatus;
import com.hotel.hotelbooking.model.enums.RoomStatus;
import com.hotel.hotelbooking.repository.CheckOutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CheckOutService {

    private final CheckOutRepository checkOutRepository;
    private final BookingService bookingService;

    public CheckOutService(CheckOutRepository checkOutRepository, BookingService bookingService) {
        this.checkOutRepository = checkOutRepository;
        this.bookingService = bookingService;
    }

    public List<Booking> todaysCheckOuts() {
        return bookingService.findTodaysCheckOuts();
    }

    /**
     * Performs check-out: Booking -> CHECKED_OUT, Room -> CLEANING.
     */
    @Transactional
    public CheckOut checkOut(Long bookingId, User staff, String remarks) {
        Booking booking = bookingService.getById(bookingId);

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new BookingException("Only a CHECKED_IN booking can be checked out.");
        }

        booking.setStatus(BookingStatus.CHECKED_OUT);
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.CLEANING);
        bookingService.save(booking);

        CheckOut checkOut = new CheckOut();
        checkOut.setBooking(booking);
        checkOut.setStaff(staff);
        checkOut.setRemarks(remarks);
        return checkOutRepository.save(checkOut);
    }
}
