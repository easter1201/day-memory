package com.daymemory.controller;

import com.daymemory.domain.dto.VerificationDto;
import com.daymemory.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    /**
     * 이메일 인증 메일 발송
     * POST /api/verification/send-email
     */
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
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<VerificationDto.Response> confirmPasswordReset(
            @RequestBody VerificationDto.PasswordResetConfirmRequest request) {
        VerificationDto.Response response = verificationService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}
