package com.example.Demo.controller;

import com.example.Demo.service.AdminService;
import com.example.Demo.controller.request.AddPlanRequest;
import com.example.Demo.controller.request.UpdatePlanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/add-plan")
    public String addPlan(@RequestBody AddPlanRequest addPlanRequest) {
        return adminService.addPlan(addPlanRequest);
    }

    @PutMapping("/update-plan")
    public String updatePlan(@RequestBody UpdatePlanRequest updatePlanRequest) {
        return adminService.updatePlan(updatePlanRequest);
    }
}
