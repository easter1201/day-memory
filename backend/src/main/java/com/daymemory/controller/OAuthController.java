package com.daymemory.controller;

import com.daymemory.domain.dto.OAuthDto;
import com.daymemory.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oauthService;

    /**
     * Google OAuth 로그인
     * POST /api/auth/google
     */
    @PostMapping("/google")
    public ResponseEntity<OAuthDto.OAuthLoginResponse> googleLogin(
            @RequestBody OAuthDto.OAuthLoginRequest request) {
        OAuthDto.OAuthLoginResponse response = oauthService.googleLogin(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Kakao OAuth 로그인
     * POST /api/auth/kakao
     */
    @PostMapping("/kakao")
    public ResponseEntity<OAuthDto.OAuthLoginResponse> kakaoLogin(
            @RequestBody OAuthDto.OAuthLoginRequest request) {
        OAuthDto.OAuthLoginResponse response = oauthService.kakaoLogin(request);
        return ResponseEntity.ok(response);
    }
}
