package com.hotel.hotelbooking.controller;

import com.hotel.hotelbooking.dto.RoomDto;
import com.hotel.hotelbooking.dto.RoomTypeDto;
import com.hotel.hotelbooking.model.enums.RoomStatus;
import com.hotel.hotelbooking.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DashboardService dashboardService;
    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    private final UserService userService;
    private final BookingService bookingService;

    public AdminController(DashboardService dashboardService, RoomService roomService,
                            RoomTypeService roomTypeService, UserService userService,
                            BookingService bookingService) {
        this.dashboardService = dashboardService;
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", dashboardService.buildStats());
        return "admin/dashboard";
    }

    @GetMapping("/rooms")
    public String rooms(Model model) {
        model.addAttribute("rooms", roomService.getAll());
        model.addAttribute("roomTypes", roomTypeService.getAll());
        if (!model.containsAttribute("roomDto")) {
            model.addAttribute("roomDto", new RoomDto());
        }
        if (!model.containsAttribute("roomTypeDto")) {
            model.addAttribute("roomTypeDto", new RoomTypeDto());
        }
        model.addAttribute("roomStatuses", RoomStatus.values());
        return "admin/rooms";
    }

    @GetMapping("/rooms/add")
    public String addRoomForm(Model model) {
        if (!model.containsAttribute("roomDto")) {
            model.addAttribute("roomDto", new RoomDto());
        }
        model.addAttribute("roomTypes", roomTypeService.getAll());
        model.addAttribute("roomStatuses", RoomStatus.values());
        return "rooms/add";
    }

    @PostMapping("/rooms/add")
    public String addRoom(@Valid @ModelAttribute("roomDto") RoomDto roomDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return addRoomForm(model);
        }
        roomService.create(roomDto);
        return "redirect:/admin/rooms";
    }

    @GetMapping("/rooms/{id}/edit")
    public String editRoomForm(@PathVariable Long id, Model model) {
        var room = roomService.getById(id);
        RoomDto dto = new RoomDto();
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomTypeId(room.getRoomType().getId());
        dto.setFloor(room.getFloor());
        dto.setStatus(room.getStatus());
        dto.setActive(room.isActive());

        model.addAttribute("roomId", id);
        model.addAttribute("roomDto", dto);
        model.addAttribute("roomTypes", roomTypeService.getAll());
        model.addAttribute("roomStatuses", RoomStatus.values());
        return "rooms/edit";
    }

    @PostMapping("/rooms/{id}/edit")
    public String editRoom(@PathVariable Long id, @Valid @ModelAttribute("roomDto") RoomDto roomDto,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return editRoomForm(id, model);
        }
        roomService.update(id, roomDto);
        return "redirect:/admin/rooms";
    }

    @PostMapping("/rooms/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam RoomStatus status) {
        roomService.changeStatus(id, status);
        return "redirect:/admin/rooms";
    }

    @PostMapping("/rooms/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id, @RequestParam boolean active) {
        roomService.setActive(id, active);
        return "redirect:/admin/rooms";
    }

    @PostMapping("/room-types/add")
    public String addRoomType(@Valid @ModelAttribute("roomTypeDto") RoomTypeDto roomTypeDto,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return rooms(model);
        }
        roomTypeService.create(roomTypeDto);
        return "redirect:/admin/rooms";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        model.addAttribute("bookings", bookingService.getAll());
        return "bookings/list";
    }
}
