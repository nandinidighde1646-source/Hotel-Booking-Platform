package com.hotel.hotelbooking.repository;

import com.hotel.hotelbooking.model.CheckOut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckOutRepository extends JpaRepository<CheckOut, Long> {

    Optional<CheckOut> findByBookingId(Long bookingId);
}
