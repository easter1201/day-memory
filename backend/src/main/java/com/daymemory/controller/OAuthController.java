package com.daymemory.controller;

import com.daymemory.domain.dto.OAuthDto;
import com.daymemory.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OAuth", description = "소셜 로그인 API - Google, Kakao 등의 OAuth 인증을 처리합니다.")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oauthService;

    /**
     * Google OAuth 로그인
     * POST /api/auth/google
     */
    @Operation(summary = "Google OAuth 로그인", description = "Google 계정으로 로그인합니다. ID 토큰을 사용하여 인증하고, 신규 사용자는 자동으로 회원가입됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = OAuthDto.OAuthLoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 OAuth 토큰",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "OAuth 인증 서버 오류",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
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
    @Operation(summary = "Kakao OAuth 로그인", description = "Kakao 계정으로 로그인합니다. 액세스 토큰을 사용하여 인증하고, 신규 사용자는 자동으로 회원가입됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = OAuthDto.OAuthLoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 OAuth 토큰",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "OAuth 인증 서버 오류",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/kakao")
    public ResponseEntity<OAuthDto.OAuthLoginResponse> kakaoLogin(
            @RequestBody OAuthDto.OAuthLoginRequest request) {
        OAuthDto.OAuthLoginResponse response = oauthService.kakaoLogin(request);
        return ResponseEntity.ok(response);
    }
}
