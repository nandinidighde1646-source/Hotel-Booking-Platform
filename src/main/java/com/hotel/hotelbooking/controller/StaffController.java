package com.hotel.hotelbooking.controller;

import com.hotel.hotelbooking.service.BookingService;
import com.hotel.hotelbooking.service.CheckInService;
import com.hotel.hotelbooking.service.CheckOutService;
import com.hotel.hotelbooking.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final DashboardService dashboardService;
    private final CheckInService checkInService;
    private final CheckOutService checkOutService;
    private final BookingService bookingService;

    public StaffController(DashboardService dashboardService, CheckInService checkInService,
                            CheckOutService checkOutService, BookingService bookingService) {
        this.dashboardService = dashboardService;
        this.checkInService = checkInService;
        this.checkOutService = checkOutService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", dashboardService.buildStats());
        model.addAttribute("recentBookings", bookingService.recentBookings());
        return "staff/dashboard";
    }

    @GetMapping("/check-ins")
    public String checkIns(Model model) {
        model.addAttribute("bookings", checkInService.todaysCheckIns());
        return "checkin/list";
    }

    @GetMapping("/check-outs")
    public String checkOuts(Model model) {
        model.addAttribute("bookings", checkOutService.todaysCheckOuts());
        return "checkout/list";
    }
}
