package com.hotel.hotelbooking.service;

import com.hotel.hotelbooking.dto.RoomDto;
import com.hotel.hotelbooking.exception.BookingException;
import com.hotel.hotelbooking.exception.ResourceNotFoundException;
import com.hotel.hotelbooking.model.Room;
import com.hotel.hotelbooking.model.RoomType;
import com.hotel.hotelbooking.model.enums.RoomStatus;
import com.hotel.hotelbooking.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeService roomTypeService;

    public RoomService(RoomRepository roomRepository, RoomTypeService roomTypeService) {
        this.roomRepository = roomRepository;
        this.roomTypeService = roomTypeService;
    }

    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    public Room getById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + id));
    }

    /**
     * Core availability search used by the public "Search Rooms" form.
     * Validates the date range, then delegates to the repository query
     * that excludes rooms with overlapping non-cancelled bookings.
     */
    public List<Room> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut, int guests) {
        validateDateRange(checkIn, checkOut);
        return roomRepository.findAvailableRooms(checkIn, checkOut, guests);
    }

    public void validateDateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new BookingException("Both check-in and check-out dates are required.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new BookingException("Check-in date cannot be in the past.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new BookingException("Check-out date must be after check-in date.");
        }
    }

    public Room create(RoomDto dto) {
        if (roomRepository.existsByRoomNumber(dto.getRoomNumber())) {
            throw new BookingException("A room with this number already exists.");
        }
        RoomType roomType = roomTypeService.getById(dto.getRoomTypeId());

        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomType(roomType);
        room.setFloor(dto.getFloor());
        room.setStatus(dto.getStatus() == null ? RoomStatus.AVAILABLE : dto.getStatus());
        room.setActive(dto.isActive());
        return roomRepository.save(room);
    }

    public Room update(Long id, RoomDto dto) {
        Room room = getById(id);
        RoomType roomType = roomTypeService.getById(dto.getRoomTypeId());

        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomType(roomType);
        room.setFloor(dto.getFloor());
        if (dto.getStatus() != null) {
            room.setStatus(dto.getStatus());
        }
        room.setActive(dto.isActive());
        return roomRepository.save(room);
    }

    public Room changeStatus(Long id, RoomStatus status) {
        Room room = getById(id);
        room.setStatus(status);
        return roomRepository.save(room);
    }

    public Room setActive(Long id, boolean active) {
        Room room = getById(id);
        room.setActive(active);
        return roomRepository.save(room);
    }

    public long countByStatus(RoomStatus status) {
        return roomRepository.countByStatus(status);
    }

    public long countAll() {
        return roomRepository.count();
    }
}
