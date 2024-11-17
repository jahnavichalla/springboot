package com.example.Demo.service;

import com.example.Demo.model.Booking;
import com.example.Demo.model.Room;
import com.example.Demo.model.User;
import com.example.Demo.repository.BookingRepository;
import com.example.Demo.repository.FloorRepository;
import com.example.Demo.repository.RoomRepository;
import com.example.Demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FloorRepository floorRepository;

    private String loggedInUserUsername;

    public UserService(String loggedInUserUsername) {
        this.loggedInUserUsername = loggedInUserUsername;
    }

    public void userMenu(Scanner sc) {
        boolean userRunning = true;
        while (userRunning) {
            System.out.println("---------- User Menu ----------");
            System.out.println("    1. View Available Rooms");
            System.out.println("    2. Recommend Room");
            System.out.println("    3. Logout");
            System.out.println("-------------------------------");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    viewAvailableRooms(sc);
                    break;
                case 2:
                    recommendRoom(sc);
                    break;
                case 3:
                    userRunning = false;
                    System.out.println("\n[SUCCESS] Logging out...\n");
                    break;
                default:
                    System.out.println("\n[ERROR] Invalid choice. Try again!!\n");
            }
        }
    }

    public void viewAvailableRooms(Scanner sc) {
        System.out.print("\nEnter Start Time (HH:MM): ");
        LocalTime startTime = LocalTime.parse(sc.nextLine());
        System.out.print("Enter End Time (HH:MM): ");
        LocalTime endTime = LocalTime.parse(sc.nextLine());

        List<Room> availableRooms = roomRepository.findAvailableRoomsForTimeSlot(startTime, endTime);
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms are available for the specified time range.");
        } else {
            System.out.println("\nAvailable Rooms:");
            availableRooms.forEach(room -> {
                System.out.println("Room: " + room.getRoomName() + ", Floor: " + room.getFloor().getFloorNumber() + ", Capacity: " + room.getCapacity());
            });
        }
    }

    public void recommendRoom(Scanner sc) {
        System.out.print("\nEnter Start Time (HH:MM): ");
        LocalTime startTime = LocalTime.parse(sc.nextLine());
        System.out.print("Enter End Time (HH:MM): ");
        LocalTime endTime = LocalTime.parse(sc.nextLine());
        System.out.print("Enter Minimum Capacity: ");
        int minCapacity = sc.nextInt();
        sc.nextLine();  // Consume newline
        System.out.print("Enter Preferred Floor Number: ");
        int preferredFloorNumber = sc.nextInt();
        sc.nextLine();  // Consume newline

        List<Room> recommendedRooms = roomRepository.findRoomsByFloorAndCapacity(preferredFloorNumber, minCapacity, startTime, endTime);
        if (recommendedRooms.isEmpty()) {
            System.out.println("No suitable rooms available.");
        } else {
            System.out.println("Recommended Rooms:");
            recommendedRooms.forEach(room -> System.out.println("Room: " + room.getRoomName() + " (Capacity: " + room.getCapacity() + ")"));
        }
    }

    public String bookRoom(int roomId, String startTime, String endTime, String user_name) {
        Optional<User> userOptional = userRepository.findByUsername(user_name);
        if (userOptional.isEmpty()) {
            return "User not found!";
        }
        User user = userOptional.get();

        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isEmpty() || !roomOptional.get().isAvailable()) {
            return "Room not available!";
        }
        Room room = roomOptional.get();

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setStartTime(LocalTime.parse(startTime));
        booking.setEndTime(LocalTime.parse(endTime));
        booking.setUser(user);
        booking.setFloor(room.getFloor());

        bookingRepository.save(booking);
        room.setAvailable(false);
        roomRepository.save(room);

        return "Room booked successfully!";
    }
}
