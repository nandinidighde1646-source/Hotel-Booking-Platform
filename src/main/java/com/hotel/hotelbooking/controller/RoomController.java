package com.hotel.hotelbooking.controller;

import com.hotel.hotelbooking.dto.RoomSearchDto;
import com.hotel.hotelbooking.model.Room;
import com.hotel.hotelbooking.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * GET /rooms                -> browse all active rooms
     * GET /rooms?checkInDate=&checkOutDate=&guests=  -> search results
     * (Used both from the homepage search form and the rooms page itself.)
     */
    @GetMapping
    public String list(@ModelAttribute("roomSearchDto") RoomSearchDto searchDto,
                        BindingResult bindingResult,
                        Model model) {

        boolean hasSearchParams = searchDto.getCheckInDate() != null && searchDto.getCheckOutDate() != null;

        List<Room> rooms;
        if (hasSearchParams) {
            int guests = searchDto.getGuests() == null ? 1 : searchDto.getGuests();
            rooms = roomService.searchAvailableRooms(searchDto.getCheckInDate(), searchDto.getCheckOutDate(), guests);
            model.addAttribute("searched", true);
        } else {
            rooms = roomService.getAll().stream().filter(Room::isActive).toList();
            model.addAttribute("searched", false);
        }

        model.addAttribute("rooms", rooms);
        model.addAttribute("roomSearchDto", searchDto);
        return "rooms/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                          @RequestParam(required = false) LocalDate checkInDate,
                          @RequestParam(required = false) LocalDate checkOutDate,
                          Model model) {
        Room room = roomService.getById(id);
        model.addAttribute("room", room);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        return "rooms/detail";
    }
}
