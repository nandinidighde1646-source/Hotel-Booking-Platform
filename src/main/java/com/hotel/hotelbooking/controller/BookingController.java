package com.hotel.hotelbooking.controller;

import com.hotel.hotelbooking.model.Booking;
import com.hotel.hotelbooking.model.Guest;
import com.hotel.hotelbooking.model.Room;
import com.hotel.hotelbooking.service.BookingService;
import com.hotel.hotelbooking.service.GuestService;
import com.hotel.hotelbooking.service.RoomService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final GuestService guestService;

    public BookingController(BookingService bookingService, RoomService roomService, GuestService guestService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.guestService = guestService;
    }

    @GetMapping
    public String redirectToHistory() {
        return "redirect:/guest/bookings";
    }

    /**
     * Shows the booking confirmation page: room, dates, nights, price,
     * total amount and the guest's own details, before they confirm.
     */
    @GetMapping("/create")
    public String createForm(@RequestParam Long roomId,
                              @RequestParam LocalDate checkInDate,
                              @RequestParam LocalDate checkOutDate,
                              Model model) {
        roomService.validateDateRange(checkInDate, checkOutDate);
        Room room = roomService.getById(roomId);
        Guest guest = currentGuest();

        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        BigDecimal total = room.getRoomType().getBasePrice().multiply(BigDecimal.valueOf(nights));

        model.addAttribute("room", room);
        model.addAttribute("guest", guest);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        model.addAttribute("nights", nights);
        model.addAttribute("totalAmount", total);
        return "bookings/create";
    }

    @PostMapping("/create")
    public String create(@RequestParam Long roomId,
                          @RequestParam LocalDate checkInDate,
                          @RequestParam LocalDate checkOutDate) {
        Guest guest = currentGuest();
        Booking booking = bookingService.createBooking(guest, roomId, checkInDate, checkOutDate);
        return "redirect:/bookings/" + booking.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getById(id);
        model.addAttribute("booking", booking);
        return "bookings/detail";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isStaffOrAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF") || a.getAuthority().equals("ROLE_ADMIN"));

        Long guestId = isStaffOrAdmin ? null : currentGuest().getId();
        bookingService.cancel(id, guestId, isStaffOrAdmin);
        return "redirect:/bookings/" + id;
    }

    private Guest currentGuest() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return guestService.getByUserEmail(email);
    }
}
