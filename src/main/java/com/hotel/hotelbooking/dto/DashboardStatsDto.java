package com.hotel.hotelbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Plain aggregate object used to render the Admin/Staff dashboards.
 * All values are computed live from the database - nothing hardcoded.
 */
@Getter
@Setter
@AllArgsConstructor
public class DashboardStatsDto {
    private long totalRooms;
    private long availableRooms;
    private long occupiedRooms;
    private long activeBookings;
    private long todaysCheckIns;
    private long todaysCheckOuts;
    private long totalGuests;
    private BigDecimal revenue;

    // Lists used to render tables on the dashboards
    private List<?> recentBookings;
    private List<?> todaysCheckInList;
    private List<?> todaysCheckOutList;
}
