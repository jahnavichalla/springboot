package com.example.Demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "floor_plan_versions")
public class FloorPlanVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "floor_plan_id", nullable = false)
    private FloorPlan floorPlan;

    @Column(name = "finalised_by", nullable = false)
    private String finalisedBy;

    @Column(nullable = false)
    private int priority;

    @Column(name = "number_of_rooms")
    private Integer numberOfRooms;

    @Column(name = "room_details", columnDefinition = "TEXT")
    private String roomDetails;

    @Column(name = "version_timestamp", nullable = false)
    private LocalDateTime versionTimestamp = LocalDateTime.now();

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FloorPlan getFloorPlan() {
        return floorPlan;
    }

    public void setFloorPlan(FloorPlan floorPlan) {
        this.floorPlan = floorPlan;
    }

    public String getFinalisedBy() {
        return finalisedBy;
    }

    public void setFinalisedBy(String finalisedBy) {
        this.finalisedBy = finalisedBy;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(Integer numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public String getRoomDetails() {
        return roomDetails;
    }

    public void setRoomDetails(String roomDetails) {
        this.roomDetails = roomDetails;
    }

    public LocalDateTime getVersionTimestamp() {
        return versionTimestamp;
    }

    public void setVersionTimestamp(LocalDateTime versionTimestamp) {
        this.versionTimestamp = versionTimestamp;
    }
}
