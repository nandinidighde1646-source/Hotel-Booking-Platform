package com.hotel.hotelbooking.repository;

import com.hotel.hotelbooking.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByUserId(Long userId);

    Optional<Guest> findByUserEmail(String email);

    long count();
}
