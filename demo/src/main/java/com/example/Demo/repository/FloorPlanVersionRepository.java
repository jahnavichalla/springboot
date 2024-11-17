package com.example.Demo.repository;

import com.example.Demo.model.FloorPlanVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorPlanVersionRepository extends JpaRepository<FloorPlanVersion, Integer> {
    // Additional custom methods can be added here if needed
}
