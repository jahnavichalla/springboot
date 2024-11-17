package com.example.Demo.repository;

import com.example.Demo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByAvailable(boolean available);
    List<Room> findByFloorId(int floorId);
}
