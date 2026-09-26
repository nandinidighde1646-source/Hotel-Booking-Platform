package com.hotel.hotelbooking.controller;

import com.hotel.hotelbooking.dto.RoomSearchDto;
import com.hotel.hotelbooking.repository.RoomRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final RoomRepository roomRepository;

    public HomeController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        if (!model.containsAttribute("roomSearchDto")) {
            model.addAttribute("roomSearchDto", new RoomSearchDto());
        }
        // Show a handful of active rooms as "featured" on the homepage
        List<?> featuredRooms = roomRepository.findAll().stream()
                .filter(r -> r.isActive())
                .limit(6)
                .toList();
        model.addAttribute("featuredRooms", featuredRooms);
        return "index";
    }
}
