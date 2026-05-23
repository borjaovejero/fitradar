package com.fitradar.backend.dashboard.controller;

import com.fitradar.backend.dashboard.dto.DashboardResponse;
import com.fitradar.backend.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // GET /api/dashboard
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {
        DashboardResponse response = dashboardService.getDashboard(authentication.getName());
        return ResponseEntity.ok(response);
    }
}