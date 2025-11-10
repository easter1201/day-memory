package com.daymemory.controller;

import com.daymemory.domain.dto.DashboardDto;
import com.daymemory.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 대시보드 요약 정보 조회
     * GET /api/dashboard?userId={userId}
     */
    @GetMapping
    public ResponseEntity<DashboardDto> getDashboardSummary(@RequestParam Long userId) {
        DashboardDto dashboard = dashboardService.getDashboardSummary(userId);
        return ResponseEntity.ok(dashboard);
    }
}
