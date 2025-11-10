package com.daymemory.controller;

import com.daymemory.domain.dto.UserDto;
import com.daymemory.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     */
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
    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getMyInfo() {
        Long userId = userService.getCurrentUserId();
        UserDto.Response response = userService.getMyInfo(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 정보 수정
     */
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
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody UserDto.PasswordChangeRequest request
    ) {
        Long userId = userService.getCurrentUserId();
        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }
}
