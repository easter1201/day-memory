package com.daymemory.controller;

import com.daymemory.domain.dto.UserDto;
import com.daymemory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 인증 및 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (이메일 중복 등)",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<UserDto.Response> signup(
            @Valid @RequestBody UserDto.SignupRequest request
    ) {
        UserDto.Response response = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인
     */
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 액세스 토큰과 리프레시 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 이메일 또는 비밀번호)",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<UserDto.LoginResponse> login(
            @Valid @RequestBody UserDto.LoginRequest request
    ) {
        UserDto.LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 토큰 재발급
     */
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.RefreshResponse.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<UserDto.RefreshResponse> refresh(
            @Valid @RequestBody UserDto.RefreshRequest request
    ) {
        UserDto.RefreshResponse response = userService.refresh(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 정보 조회
     */
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.Response.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getMyInfo() {
        Long userId = userService.getCurrentUserId();
        UserDto.Response response = userService.getMyInfo(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 정보 수정
     */
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PutMapping("/me")
    public ResponseEntity<UserDto.Response> updateMyInfo(
            @Valid @RequestBody UserDto.UpdateRequest request
    ) {
        Long userId = userService.getCurrentUserId();
        UserDto.Response response = userService.updateMyInfo(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 변경
     */
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다. 현재 비밀번호를 확인한 후 새로운 비밀번호로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (현재 비밀번호 불일치 등)",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody UserDto.PasswordChangeRequest request
    ) {
        Long userId = userService.getCurrentUserId();
        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃", description = "현재 로그인한 사용자를 로그아웃합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        Long userId = userService.getCurrentUserId();
        userService.logout(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 회원 탈퇴
     */
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteAccount() {
        Long userId = userService.getCurrentUserId();
        userService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 알림 설정 조회
     */
    @Operation(summary = "알림 설정 조회", description = "현재 로그인한 사용자의 알림 설정을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.NotificationSettingsResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/notification-settings")
    public ResponseEntity<UserDto.NotificationSettingsResponse> getNotificationSettings() {
        Long userId = userService.getCurrentUserId();
        UserDto.NotificationSettingsResponse response = userService.getNotificationSettings(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 알림 설정 수정
     */
    @Operation(summary = "알림 설정 수정", description = "현재 로그인한 사용자의 알림 설정을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.NotificationSettingsResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PutMapping("/notification-settings")
    public ResponseEntity<UserDto.NotificationSettingsResponse> updateNotificationSettings(
            @Valid @RequestBody UserDto.NotificationSettingsRequest request
    ) {
        Long userId = userService.getCurrentUserId();
        UserDto.NotificationSettingsResponse response = userService.updateNotificationSettings(userId, request);
        return ResponseEntity.ok(response);
    }
}
