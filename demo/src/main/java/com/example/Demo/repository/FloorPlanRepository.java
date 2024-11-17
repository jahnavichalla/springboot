package com.example.Demo.repository;

import com.example.Demo.model.FloorPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorPlanRepository extends JpaRepository<FloorPlan, Integer> {
    FloorPlan findByFloorIdAndIsFinalised(int floorId, boolean isFinalised);
}
