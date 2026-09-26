package com.hotel.hotelbooking.service;

import com.hotel.hotelbooking.dto.RegisterDto;
import com.hotel.hotelbooking.exception.BookingException;
import com.hotel.hotelbooking.exception.ResourceNotFoundException;
import com.hotel.hotelbooking.model.Guest;
import com.hotel.hotelbooking.model.User;
import com.hotel.hotelbooking.model.enums.UserRole;
import com.hotel.hotelbooking.repository.GuestRepository;
import com.hotel.hotelbooking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, GuestRepository guestRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.guestRepository = guestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Public self-registration. Always creates a GUEST role user plus
     * a linked Guest profile. Admin/Staff accounts are created only via
     * seed data or an Admin-only endpoint.
     */
    @Transactional
    public User registerGuest(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BookingException("An account with this email already exists.");
        }

        User user = new User();
        user.setName(dto.getFirstName() + " " + dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // BCrypt hash, never plain text
        user.setRole(UserRole.GUEST);
        user.setEnabled(true);
        user = userRepository.save(user);

        Guest guest = new Guest();
        guest.setUser(user);
        guest.setFirstName(dto.getFirstName());
        guest.setLastName(dto.getLastName());
        guest.setEmail(dto.getEmail());
        guest.setPhone(dto.getPhone());
        guest.setAddress(dto.getAddress());
        guest.setIdProof(dto.getIdProof());
        guestRepository.save(guest);

        return user;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
