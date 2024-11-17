package com.example.Demo.controller.request;

import com.example.Demo.model.Room;
import java.util.List;

public class UpdatePlanRequest {
    private int floorNumber;
    private List<Room> updatedRooms;
    private String adminName;
    private int adminPriority;

    // Getters and Setters
    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public List<Room> getUpdatedRooms() {
        return updatedRooms;
    }

    public void setUpdatedRooms(List<Room> updatedRooms) {
        this.updatedRooms = updatedRooms;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public int getAdminPriority() {
        return adminPriority;
    }

    public void setAdminPriority(int adminPriority) {
        this.adminPriority = adminPriority;
    }
}
