package com.hotel.hotelbooking.controller;

import com.hotel.hotelbooking.model.User;
import com.hotel.hotelbooking.service.CheckInService;
import com.hotel.hotelbooking.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/check-in")
public class CheckInController {

    private final CheckInService checkInService;
    private final UserService userService;

    public CheckInController(CheckInService checkInService, UserService userService) {
        this.checkInService = checkInService;
        this.userService = userService;
    }

    @PostMapping("/{bookingId}")
    public String checkIn(@PathVariable Long bookingId,
                           @RequestParam(required = false) String remarks) {
        User staff = currentStaff();
        checkInService.checkIn(bookingId, staff, remarks);
        return "redirect:/staff/check-ins";
    }

    private User currentStaff() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getByEmail(email);
    }
}
