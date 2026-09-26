package com.hotel.hotelbooking.dto;

import com.hotel.hotelbooking.model.enums.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDto {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private Long roomTypeId;

    @NotNull(message = "Floor is required")
    private Integer floor;

    private RoomStatus status = RoomStatus.AVAILABLE;

    private boolean active = true;
}
