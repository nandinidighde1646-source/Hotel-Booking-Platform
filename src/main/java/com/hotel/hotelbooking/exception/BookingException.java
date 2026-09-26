package com.hotel.hotelbooking.exception;

/**
 * Thrown for any business-rule violation: invalid dates, room not
 * available, double booking, email already registered, etc.
 */
public class BookingException extends RuntimeException {
    public BookingException(String message) {
        super(message);
    }
}
