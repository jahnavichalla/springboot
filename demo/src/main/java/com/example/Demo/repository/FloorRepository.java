package com.example.Demo.repository;

import com.example.Demo.model.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Integer> {
    Floor findByFloorNumber(int floorNumber);
}
