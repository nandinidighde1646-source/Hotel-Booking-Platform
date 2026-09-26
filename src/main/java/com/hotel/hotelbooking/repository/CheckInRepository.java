package com.hotel.hotelbooking.repository;

import com.hotel.hotelbooking.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    Optional<CheckIn> findByBookingId(Long bookingId);
}
