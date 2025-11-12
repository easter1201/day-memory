package com.daymemory.controller;

import com.daymemory.domain.dto.DashboardDto;
import com.daymemory.security.SecurityUtils;
import com.daymemory.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dashboard", description = "대시보드 API - 사용자의 주요 정보를 한눈에 조회합니다.")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 대시보드 요약 정보 조회
     * GET /api/dashboard
     */
    @Operation(summary = "대시보드 요약 정보 조회", description = "로그인한 사용자의 이벤트, 선물, 리마인더 등의 요약 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대시보드 조회 성공",
                    content = @Content(schema = @Schema(implementation = DashboardDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<DashboardDto> getDashboardSummary() {
        Long userId = SecurityUtils.getCurrentUserId();
        DashboardDto dashboard = dashboardService.getDashboardSummary(userId);
        return ResponseEntity.ok(dashboard);
    }
}
