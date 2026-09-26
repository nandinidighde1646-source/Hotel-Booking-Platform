package com.hotel.hotelbooking.controller;

import com.hotel.hotelbooking.model.Guest;
import com.hotel.hotelbooking.service.BookingService;
import com.hotel.hotelbooking.service.GuestService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/guest")
public class GuestController {

    private final GuestService guestService;
    private final BookingService bookingService;

    public GuestController(GuestService guestService, BookingService bookingService) {
        this.guestService = guestService;
        this.bookingService = bookingService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Guest guest = currentGuest();
        model.addAttribute("guest", guest);
        return "guest/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("guest") Guest formGuest,
                                 BindingResult bindingResult, Model model) {
        Guest guest = currentGuest();
        if (bindingResult.hasErrors()) {
            model.addAttribute("guest", guest);
            return "guest/profile";
        }
        guest.setFirstName(formGuest.getFirstName());
        guest.setLastName(formGuest.getLastName());
        guest.setPhone(formGuest.getPhone());
        guest.setAddress(formGuest.getAddress());
        guest.setIdProof(formGuest.getIdProof());
        guestService.update(guest);
        model.addAttribute("guest", guest);
        model.addAttribute("successMessage", "Profile updated successfully.");
        return "guest/profile";
    }

    @GetMapping("/bookings")
    public String bookingHistory(Model model) {
        Guest guest = currentGuest();
        var bookings = bookingService.getHistoryForGuest(guest.getId());
        BigDecimal totalSpent = bookings.stream()
                .filter(b -> b.getStatus().name().equals("CHECKED_OUT"))
                .map(b -> b.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("bookings", bookings);
        model.addAttribute("totalSpent", totalSpent);
        return "guest/bookings";
    }

    private Guest currentGuest() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return guestService.getByUserEmail(email);
    }
}
