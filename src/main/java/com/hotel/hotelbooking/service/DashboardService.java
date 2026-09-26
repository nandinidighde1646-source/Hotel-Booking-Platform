package com.hotel.hotelbooking.service;

import com.hotel.hotelbooking.dto.DashboardStatsDto;
import com.hotel.hotelbooking.model.enums.RoomStatus;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final RoomService roomService;
    private final BookingService bookingService;
    private final GuestService guestService;

    public DashboardService(RoomService roomService, BookingService bookingService, GuestService guestService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.guestService = guestService;
    }

    /**
     * Builds the full set of dashboard numbers from live DB queries.
     * Nothing here is hardcoded.
     */
    public DashboardStatsDto buildStats() {
        long totalRooms = roomService.countAll();
        long availableRooms = roomService.countByStatus(RoomStatus.AVAILABLE);
        long occupiedRooms = roomService.countByStatus(RoomStatus.OCCUPIED);
        long activeBookings = bookingService.countActiveBookings();
        var todaysCheckIns = bookingService.findTodaysCheckIns();
        var todaysCheckOuts = bookingService.findTodaysCheckOuts();
        long totalGuests = guestService.countAll();
        var revenue = bookingService.totalRevenue();
        var recentBookings = bookingService.recentBookings();

        return new DashboardStatsDto(
                totalRooms,
                availableRooms,
                occupiedRooms,
                activeBookings,
                todaysCheckIns.size(),
                todaysCheckOuts.size(),
                totalGuests,
                revenue,
                recentBookings,
                todaysCheckIns,
                todaysCheckOuts
        );
    }
}
