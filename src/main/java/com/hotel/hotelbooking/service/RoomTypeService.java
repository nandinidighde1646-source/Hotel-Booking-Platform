package com.hotel.hotelbooking.service;

import com.hotel.hotelbooking.dto.RoomTypeDto;
import com.hotel.hotelbooking.exception.BookingException;
import com.hotel.hotelbooking.exception.ResourceNotFoundException;
import com.hotel.hotelbooking.model.RoomType;
import com.hotel.hotelbooking.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    public List<RoomType> getAll() {
        return roomTypeRepository.findAll();
    }

    public RoomType getById(Long id) {
        return roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found: " + id));
    }

    public RoomType create(RoomTypeDto dto) {
        if (roomTypeRepository.existsByName(dto.getName())) {
            throw new BookingException("A room type with this name already exists.");
        }
        RoomType roomType = new RoomType();
        applyDto(roomType, dto);
        return roomTypeRepository.save(roomType);
    }

    public RoomType update(Long id, RoomTypeDto dto) {
        RoomType roomType = getById(id);
        applyDto(roomType, dto);
        return roomTypeRepository.save(roomType);
    }

    private void applyDto(RoomType roomType, RoomTypeDto dto) {
        roomType.setName(dto.getName());
        roomType.setDescription(dto.getDescription());
        roomType.setCapacity(dto.getCapacity());
        roomType.setBasePrice(dto.getBasePrice());
    }
}
