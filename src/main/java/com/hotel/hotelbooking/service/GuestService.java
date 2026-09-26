package com.hotel.hotelbooking.service;

import com.hotel.hotelbooking.exception.ResourceNotFoundException;
import com.hotel.hotelbooking.model.Guest;
import com.hotel.hotelbooking.repository.GuestRepository;
import org.springframework.stereotype.Service;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    public Guest getByUserEmail(String email) {
        return guestRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Guest profile not found for " + email));
    }

    public Guest getById(Long id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found: " + id));
    }

    public Guest update(Guest guest) {
        return guestRepository.save(guest);
    }

    public long countAll() {
        return guestRepository.count();
    }
}
