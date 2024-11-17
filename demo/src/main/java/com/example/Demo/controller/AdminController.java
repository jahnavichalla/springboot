package com.example.demo.controller;

import com.example.demo.controller.request.AddPlanRequest;
import com.example.demo.controller.request.UpdatePlanRequest;
import com.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/addPlan")
    public void addPlan(@RequestBody AddPlanRequest request) {
        adminService.addPlan(request.getFloorNumber(), request.getPlanDetails(), request.getTotalRooms());
    }

    @PutMapping("/updatePlan")
    public void updatePlan(@RequestBody UpdatePlanRequest request) {
        adminService.updatePlan(request.getFloorNumber(), request.getUpdatedPlanDetails());
    }
}
