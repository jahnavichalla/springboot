package com.example.Demo.service;

import com.example.Demo.model.Floor;
import com.example.Demo.model.FloorPlan;
import com.example.Demo.model.FloorPlanVersion;
import com.example.Demo.model.Room;
import com.example.Demo.repository.FloorPlanRepository;
import com.example.Demo.repository.FloorPlanVersionRepository;
import com.example.Demo.repository.FloorRepository;
import com.example.Demo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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

    public void adminMenu(Scanner sc) {
        boolean adminRunning = true;
        while (adminRunning) {
            System.out.println("------- Admin Menu -------");
            System.out.println("      1. Add Plan");
            System.out.println("      2. Update Plan");
            System.out.println("      3. Exit");
            System.out.println("--------------------------");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    addPlan(sc);
                    break;
                case 2:
                    updatePlan(sc);
                    break;
                case 3:
                    adminRunning = false;
                    System.out.println("\n[SUCCESS] Exiting Admin Menu...\n");
                    break;
                default:
                    System.out.println("\n[ERROR] Invalid choice. Try again!!\n");
            }
        }
    }

    public void addPlan(Scanner sc) {
        System.out.println("\n------------------------- Adding a New Plan -------------------------");
        System.out.print("Enter floor number: ");
        int floorNumber = sc.nextInt();
        sc.nextLine();  // Consume newline

        // Check if the floor already exists
        if (floorRepository.findByFloorNumber(floorNumber) != null) {
            System.out.println("\n[ERROR] You can't add the plan as this floor already exists.\n");
            return;
        }

        System.out.print("\nPlease enter the total number of rooms for this floor: ");
        int totalRooms = sc.nextInt();
        sc.nextLine();  // Consume newline

        // Create and save the new floor
        Floor floor = new Floor();
        floor.setFloorNumber(floorNumber);
        floor.setTotalRooms(totalRooms);
        floor = floorRepository.save(floor);
        System.out.println("\n[SUCCESS] New floor created with ID: " + floor.getId());

        // Collect and save room details
        List<Room> rooms = new ArrayList<>();
        for (int i = 1; i <= totalRooms; i++) {
            System.out.println("\nEnter details for Room " + i + ":");
            System.out.print("Room Name: ");
            String roomName = sc.nextLine();
            System.out.print("Capacity: ");
            int capacity = sc.nextInt();
            sc.nextLine();  // Consume newline

            Room room = new Room();
            room.setRoomName(roomName);
            room.setCapacity(capacity);
            room.setAvailable(true);
            room.setFloor(floor);
            rooms.add(room);
        }
        roomRepository.saveAll(rooms);
        System.out.println("[SUCCESS] All rooms added successfully.");

        // Create and save the floor plan
        FloorPlan floorPlan = new FloorPlan();
        floorPlan.setFloor(floor);
        floorPlan.setPlanDetails("Floor " + floorNumber + " plan finalized by " + loggedInAdminUsername);
        floorPlan.setFinalisedBy(loggedInAdminUsername);
        floorPlan.setPriority(loggedInAdminPriority);
        floorPlan.setFinalised(true);
        floorPlan = floorPlanRepository.save(floorPlan);

        // Save the initial version of the floor plan
        saveFloorPlanVersion(floorPlan, rooms);
        System.out.println("\n[SUCCESS] Floor plan added successfully for floor " + floorNumber + " and finalized.\n");
    }

    public void updatePlan(Scanner sc) {
        System.out.println("\n------------------------- Updating the Plan -------------------------");
        System.out.print("Enter floor number: ");
        int floorNumber = sc.nextInt();
        sc.nextLine(); // Consume newline

        // Find the floor by number
        Floor floor = floorRepository.findByFloorNumber(floorNumber);
        if (floor == null) {
            System.out.println("\n[ERROR] There is no floor associated with the given number.\n");
            return;
        }

        // Find the finalized floor plan for the floor
        FloorPlan currentPlan = floorPlanRepository.findByFloorIdAndIsFinalised(floor.getId(), true);
        if (currentPlan == null) {
            System.out.println("\n[ERROR] There is no finalized plan available for this floor.\n");
            return;
        }

        if (loggedInAdminPriority < currentPlan.getPriority()) {
            System.out.println("\n[ERROR] Unable to update the plan due to low priority, but the version will be saved.");
            saveFloorPlanVersion(currentPlan, getRoomsForFloor(floor));
            return;
        }

        // Delete existing rooms for the floor
        roomRepository.deleteAll(roomRepository.findByFloorId(floor.getId()));

        // Prompt for new room details
        System.out.print("\nPlease enter the total number of new rooms: ");
        int numberOfRooms = sc.nextInt();
        sc.nextLine(); // Consume newline

        List<Room> updatedRooms = new ArrayList<>();
        for (int i = 1; i <= numberOfRooms; i++) {
            System.out.println("\nEnter details for Room " + i + ":");
            System.out.print("Room Name: ");
            String roomName = sc.nextLine();
            System.out.print("Capacity: ");
            int capacity = sc.nextInt();
            sc.nextLine(); // Consume newline

            Room room = new Room();
            room.setRoomName(roomName);
            room.setCapacity(capacity);
            room.setAvailable(true);
            room.setFloor(floor);
            updatedRooms.add(room);
        }
        roomRepository.saveAll(updatedRooms);

        // Update the floor plan details
        currentPlan.setPlanDetails("Updated plan for floor " + floorNumber);
        currentPlan.setFinalisedBy(loggedInAdminUsername);
        currentPlan.setPriority(loggedInAdminPriority);
        floorPlanRepository.save(currentPlan);

        System.out.println("\n[SUCCESS] Floor plan updated successfully!\n");
        saveFloorPlanVersion(currentPlan, updatedRooms);
    }

    private void saveFloorPlanVersion(FloorPlan floorPlan, List<Room> rooms) {
        FloorPlanVersion version = new FloorPlanVersion();
        version.setFloorPlan(floorPlan);
        version.setFinalisedBy(loggedInAdminUsername);
        version.setPriority(loggedInAdminPriority);
        version.setNumberOfRooms(rooms.size());

        StringBuilder roomDetailsBuilder = new StringBuilder();
        for (Room room : rooms) {
            roomDetailsBuilder.append(room.getRoomName()).append(", Capacity: ").append(room.getCapacity()).append("; ");
        }
        version.setRoomDetails(roomDetailsBuilder.toString());

        floorPlanVersionRepository.save(version);
        System.out.println("[SUCCESS] Floor plan version saved.");
    }

    private List<Room> getRoomsForFloor(Floor floor) {
        return roomRepository.findByFloorId(floor.getId());
    }
}
