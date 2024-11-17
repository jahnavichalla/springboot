package com.example.Demo.service;

import com.example.Demo.model.Floor;
import com.example.Demo.model.Room;
import com.example.Demo.model.FloorPlan;
import com.example.Demo.model.FloorPlanVersion;
import com.example.Demo.repository.FloorRepository;
import com.example.Demo.repository.RoomRepository;
import com.example.Demo.repository.FloorPlanRepository;
import com.example.Demo.repository.FloorPlanVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FloorPlanRepository floorPlanRepository;

    @Autowired
    private FloorPlanVersionRepository floorPlanVersionRepository;

    private String loggedInAdminUsername;  
    private int loggedInAdminPriority;

    public AdminService(String loggedInAdminUsername, int loggedInAdminPriority) {
        this.loggedInAdminUsername = loggedInAdminUsername;
        this.loggedInAdminPriority = loggedInAdminPriority;
    }

    // Add a new floor plan
    public String addPlan(int floorNumber, int totalRooms, List<Room> rooms) {
        Optional<Floor> floorOpt = floorRepository.findByFloorNumber(floorNumber);

        if (floorOpt.isPresent()) {
            return "Error: This floor already exists.";
        }

        // Create a new floor and save it
        Floor newFloor = new Floor(floorNumber, totalRooms);
        floorRepository.save(newFloor);

        // Save each room associated with this floor
        for (Room room : rooms) {
            room.setFloor(newFloor);
            roomRepository.save(room);
        }

        // Add the floor plan
        FloorPlan floorPlan = new FloorPlan(newFloor, "Finalized by " + loggedInAdminUsername, loggedInAdminPriority, true);
        floorPlanRepository.save(floorPlan);

        // Store initial version of the floor plan
        FloorPlanVersion version = new FloorPlanVersion(floorPlan, loggedInAdminUsername, loggedInAdminPriority, rooms.size(), getRoomDetails(rooms));
        floorPlanVersionRepository.save(version);

        return "Success: Floor plan added successfully.";
    }

    // Update an existing floor plan
    public String updatePlan(int floorNumber, int totalRooms, List<Room> updatedRooms) {
        Optional<Floor> floorOpt = floorRepository.findByFloorNumber(floorNumber);

        if (floorOpt.isEmpty()) {
            return "Error: No floor exists with the given floor number.";
        }

        Floor floor = floorOpt.get();
        Optional<FloorPlan> floorPlanOpt = floorPlanRepository.findFinalizedByFloor(floor.getId());

        if (floorPlanOpt.isEmpty()) {
            return "Error: No finalized floor plan exists for this floor.";
        }

        FloorPlan floorPlan = floorPlanOpt.get();

        if (loggedInAdminPriority < floorPlan.getPriority()) {
            saveVersion(floorPlan, updatedRooms);
            return "Error: Your priority is too low to update the plan. The new version has been saved.";
        }

        roomRepository.deleteByFloorId(floor.getId()); // Delete existing rooms

        for (Room room : updatedRooms) {
            room.setFloor(floor);
            roomRepository.save(room); // Save new rooms
        }

        floorPlan.setPlanDetails("Updated by " + loggedInAdminUsername);
        floorPlan.setPriority(loggedInAdminPriority);
        floorPlanRepository.save(floorPlan);

        saveVersion(floorPlan, updatedRooms);
        return "Success: Floor plan updated successfully.";
    }

    private void saveVersion(FloorPlan floorPlan, List<Room> rooms) {
        String roomDetails = getRoomDetails(rooms);
        FloorPlanVersion version = new FloorPlanVersion(floorPlan, loggedInAdminUsername, loggedInAdminPriority, rooms.size(), roomDetails);
        floorPlanVersionRepository.save(version);
    }

    private String getRoomDetails(List<Room> rooms) {
        StringBuilder roomDetails = new StringBuilder();
        for (Room room : rooms) {
            roomDetails.append(room.getRoomName()).append(",").append(room.getCapacity()).append(";");
        }
        return roomDetails.toString();
    }
}
