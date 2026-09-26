package com.hotel.hotelbooking.repository;

import com.hotel.hotelbooking.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    boolean existsByName(String name);
}
