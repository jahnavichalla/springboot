package com.example.demo.controller;

import com.example.demo.model.Room;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint to view available rooms in a specified time slot
    @GetMapping("/viewAvailableRooms")
    public List<Room> viewAvailableRooms(@RequestParam String startTime, @RequestParam String endTime) {
        return userService.viewAvailableRooms(startTime, endTime);
    }

    // Endpoint to recommend rooms based on user preferences
    @GetMapping("/recommendRoom")
    public List<Room> recommendRoom(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam int minCapacity,
            @RequestParam int preferredFloorNumber) {
        return userService.recommendRoom(startTime, endTime, minCapacity, preferredFloorNumber);
    }
}
