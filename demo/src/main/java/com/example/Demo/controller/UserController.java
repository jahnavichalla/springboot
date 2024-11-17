package com.example.Demo.controller;

import com.example.Demo.model.Room;
import com.example.Demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/available-rooms")
    public List<Room> viewAvailableRooms(@RequestParam String startTime, @RequestParam String endTime) {
        return userService.viewAvailableRooms(startTime, endTime);
    }

    @PostMapping("/book-room")
    public String bookRoom(
        @RequestParam int roomId,
        @RequestParam String startTime,
        @RequestParam String endTime,
        @RequestParam String username
    ) {
        return userService.bookRoom(roomId, startTime, endTime, username);
    }
}
