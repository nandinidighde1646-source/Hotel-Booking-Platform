package com.hotel.hotelbooking.controller;

import com.hotel.hotelbooking.model.User;
import com.hotel.hotelbooking.service.CheckOutService;
import com.hotel.hotelbooking.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/check-out")
public class CheckOutController {

    private final CheckOutService checkOutService;
    private final UserService userService;

    public CheckOutController(CheckOutService checkOutService, UserService userService) {
        this.checkOutService = checkOutService;
        this.userService = userService;
    }

    @PostMapping("/{bookingId}")
    public String checkOut(@PathVariable Long bookingId,
                            @RequestParam(required = false) String remarks) {
        User staff = currentStaff();
        checkOutService.checkOut(bookingId, staff, remarks);
        return "redirect:/staff/check-outs";
    }

    private User currentStaff() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getByEmail(email);
    }
}
