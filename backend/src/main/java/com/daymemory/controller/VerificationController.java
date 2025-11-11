package com.daymemory.controller;

import com.daymemory.domain.dto.VerificationDto;
import com.daymemory.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Verification", description = "인증 API - 이메일 인증 및 비밀번호 재설정을 처리합니다.")
@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    /**
     * 이메일 인증 메일 발송
     * POST /api/verification/send-email
     */
    @Operation(summary = "이메일 인증 메일 발송", description = "회원가입 시 이메일 인증을 위한 인증 코드를 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 메일 발송 성공",
                    content = @Content(schema = @Schema(implementation = VerificationDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 이메일 형식 또는 이미 인증된 이메일",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/send-email")
    public ResponseEntity<VerificationDto.Response> sendEmailVerification(
            @RequestBody VerificationDto.SendEmailVerificationRequest request) {
        VerificationDto.Response response = verificationService.sendEmailVerification(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 이메일 인증 완료
     * POST /api/verification/verify-email
     */
    @Operation(summary = "이메일 인증 완료", description = "이메일로 받은 인증 코드를 확인하여 이메일 인증을 완료합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공",
                    content = @Content(schema = @Schema(implementation = VerificationDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 인증 코드 또는 만료된 코드",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/verify-email")
    public ResponseEntity<VerificationDto.Response> verifyEmail(
            @RequestBody VerificationDto.VerifyEmailRequest request) {
        VerificationDto.Response response = verificationService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 재설정 요청
     * POST /api/verification/password-reset
     */
    @Operation(summary = "비밀번호 재설정 요청", description = "비밀번호를 잊어버린 경우, 이메일로 비밀번호 재설정 링크를 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재설정 메일 발송 성공",
                    content = @Content(schema = @Schema(implementation = VerificationDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "등록되지 않은 이메일",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/password-reset")
    public ResponseEntity<VerificationDto.Response> requestPasswordReset(
            @RequestBody VerificationDto.PasswordResetRequest request) {
        VerificationDto.Response response = verificationService.requestPasswordReset(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 재설정 완료
     * POST /api/verification/password-reset/confirm
     */
    @Operation(summary = "비밀번호 재설정 완료", description = "재설정 토큰을 확인하고 새로운 비밀번호로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공",
                    content = @Content(schema = @Schema(implementation = VerificationDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 토큰 또는 만료된 토큰",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<VerificationDto.Response> confirmPasswordReset(
            @RequestBody VerificationDto.PasswordResetConfirmRequest request) {
        VerificationDto.Response response = verificationService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}
