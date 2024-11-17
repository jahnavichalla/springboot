package com.example.Demo.service;

import com.example.Demo.model.Room;
import com.example.Demo.repository.RoomRepository;
import com.example.Demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    private String loggedInUserUsername;

    public UserService(String loggedInUserUsername) {
        this.loggedInUserUsername = loggedInUserUsername;
    }

    // View available rooms in a specified time slot
    public List<Room> viewAvailableRooms(String startTimeStr, String endTimeStr) {
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);
        return roomRepository.findAvailableRooms(startTime, endTime);
    }

    // Recommend rooms based on user preferences
    public List<Room> recommendRoom(String startTimeStr, String endTimeStr, int minCapacity, int preferredFloorNumber) {
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);

        // Step 1: Get available rooms based on time slot
        List<Room> availableRooms = roomRepository.findAvailableRoomsForTimeSlot(startTime, endTime);

        // Step 2: Filter by capacity
        List<Room> capacityFilteredRooms = availableRooms.stream()
                .filter(room -> room.getCapacity() >= minCapacity)
                .collect(Collectors.toList());

        if (capacityFilteredRooms.isEmpty()) {
            return recommendAlternativeRooms(availableRooms, minCapacity, preferredFloorNumber);
        }

        // Step 3: Filter by preferred floor
        List<Room> preferredFloorRooms = capacityFilteredRooms.stream()
                .filter(room -> room.getFloor().getFloorNumber() == preferredFloorNumber)
                .collect(Collectors.toList());

        return preferredFloorRooms.isEmpty() ? recommendAlternativeRooms(availableRooms, minCapacity, preferredFloorNumber) : preferredFloorRooms;
    }

    // Book a room for the user
    public String bookRoom(int roomId, String startTimeStr, String endTimeStr) {
        Optional<Integer> userIdOpt = userRepository.findIdByUsername(loggedInUserUsername);
        if (userIdOpt.isEmpty()) {
            return "No user found with the given username.";
        }

        int userId = userIdOpt.get();
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);

        boolean success = roomRepository.bookRoom(roomId, startTime, endTime, userId);
        return success ? "Room booked successfully!" : "Failed to book room.";
    }

    // Recommend alternative rooms on nearby floors
    private List<Room> recommendAlternativeRooms(List<Room> availableRooms, int minCapacity, int preferredFloorNumber) {
        TreeMap<Integer, List<Room>> floorDistanceMap = new TreeMap<>();

        for (Room room : availableRooms) {
            if (room.getCapacity() >= minCapacity) {
                int distance = Math.abs(preferredFloorNumber - room.getFloor().getFloorNumber());
                floorDistanceMap.computeIfAbsent(distance, k -> new ArrayList<>()).add(room);
            }
        }

        return floorDistanceMap.firstEntry() != null ? floorDistanceMap.firstEntry().getValue() : Collections.emptyList();
    }
}
