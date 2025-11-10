package com.daymemory.service;

import com.daymemory.domain.dto.VerificationDto;
import com.daymemory.domain.entity.OAuthProvider;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.entity.VerificationToken;
import com.daymemory.domain.repository.UserRepository;
import com.daymemory.domain.repository.VerificationTokenRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class VerificationService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final int EMAIL_VERIFICATION_EXPIRY_HOURS = 24;
    private static final int PASSWORD_RESET_EXPIRY_HOURS = 1;

    /**
     * 이메일 인증 메일 발송
     */
    @Transactional
    public VerificationDto.Response sendEmailVerification(VerificationDto.SendEmailVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getEmailVerified()) {
            return VerificationDto.Response.builder()
                    .success(false)
                    .message("이미 인증된 이메일입니다.")
                    .build();
        }

        // 기존 토큰이 있으면 삭제
        tokenRepository.findByUserIdAndTypeAndIsUsedFalse(
                user.getId(), VerificationToken.TokenType.EMAIL_VERIFICATION
        ).ifPresent(tokenRepository::delete);

        // 새 토큰 생성
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(EMAIL_VERIFICATION_EXPIRY_HOURS))
                .build();

        tokenRepository.save(verificationToken);

        // 이메일 발송
        String verificationLink = "http://localhost:3000/verify-email?token=" + token;
        String emailBody = buildEmailVerificationHtml(user.getName(), verificationLink);

        try {
            emailService.sendReminderEmail(
                    user.getEmail(),
                    "[Day Memory] 이메일 인증을 완료해주세요",
                    emailBody
            );
        } catch (Exception e) {
            log.error("Failed to send verification email", e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }

        return VerificationDto.Response.builder()
                .success(true)
                .message("인증 메일이 발송되었습니다.")
                .build();
    }

    /**
     * 이메일 인증 완료 처리
     */
    @Transactional
    public VerificationDto.Response verifyEmail(VerificationDto.VerifyEmailRequest request) {
        VerificationToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (token.getIsUsed()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        if (token.isExpired()) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        if (token.getType() != VerificationToken.TokenType.EMAIL_VERIFICATION) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        User user = token.getUser();
        user.verifyEmail();
        token.markAsUsed();

        return VerificationDto.Response.builder()
                .success(true)
                .message("이메일 인증이 완료되었습니다.")
                .build();
    }

    /**
     * 비밀번호 재설정 요청
     */
    @Transactional
    public VerificationDto.Response requestPasswordReset(VerificationDto.PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // OAuth 사용자는 비밀번호 재설정 불가
        if (user.getOauthProvider() != null && user.getOauthProvider() != OAuthProvider.LOCAL) {
            return VerificationDto.Response.builder()
                    .success(false)
                    .message("소셜 로그인 사용자는 비밀번호를 재설정할 수 없습니다.")
                    .build();
        }

        // 기존 토큰이 있으면 삭제
        tokenRepository.findByUserIdAndTypeAndIsUsedFalse(
                user.getId(), VerificationToken.TokenType.PASSWORD_RESET
        ).ifPresent(tokenRepository::delete);

        // 새 토큰 생성
        String token = UUID.randomUUID().toString();
        VerificationToken resetToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .type(VerificationToken.TokenType.PASSWORD_RESET)
                .expiresAt(LocalDateTime.now().plusHours(PASSWORD_RESET_EXPIRY_HOURS))
                .build();

        tokenRepository.save(resetToken);

        // 이메일 발송
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        String emailBody = buildPasswordResetHtml(user.getName(), resetLink);

        try {
            emailService.sendReminderEmail(
                    user.getEmail(),
                    "[Day Memory] 비밀번호 재설정 요청",
                    emailBody
            );
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }

        return VerificationDto.Response.builder()
                .success(true)
                .message("비밀번호 재설정 메일이 발송되었습니다.")
                .build();
    }

    /**
     * 비밀번호 재설정 완료
     */
    @Transactional
    public VerificationDto.Response resetPassword(VerificationDto.PasswordResetConfirmRequest request) {
        VerificationToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (token.getIsUsed()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        if (token.isExpired()) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        if (token.getType() != VerificationToken.TokenType.PASSWORD_RESET) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        User user = token.getUser();
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encodedPassword);
        token.markAsUsed();

        return VerificationDto.Response.builder()
                .success(true)
                .message("비밀번호가 재설정되었습니다.")
                .build();
    }

    /**
     * 이메일 인증 HTML 생성
     */
    private String buildEmailVerificationHtml(String userName, String verificationLink) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); margin: 0; padding: 40px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 16px; padding: 40px; box-shadow: 0 10px 40px rgba(0,0,0,0.1); }
                        .header { text-align: center; padding-bottom: 30px; border-bottom: 2px solid #f0f0f0; }
                        .logo { font-size: 28px; font-weight: bold; background: linear-gradient(135deg, #667eea, #764ba2); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
                        .content { padding: 30px 0; }
                        .greeting { font-size: 20px; font-weight: 600; color: #333; margin-bottom: 20px; }
                        .message { font-size: 16px; color: #666; line-height: 1.6; margin-bottom: 30px; }
                        .button { text-align: center; margin: 30px 0; }
                        .button a { display: inline-block; padding: 14px 40px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; border-radius: 8px; font-weight: 600; }
                        .footer { text-align: center; padding-top: 30px; border-top: 2px solid #f0f0f0; color: #999; font-size: 14px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <div class="logo">📅 Day Memory</div>
                        </div>
                        <div class="content">
                            <div class="greeting">안녕하세요, %s님!</div>
                            <div class="message">
                                Day Memory 회원가입을 환영합니다.<br>
                                아래 버튼을 클릭하여 이메일 인증을 완료해주세요.<br>
                                <br>
                                <strong>인증 링크는 24시간 동안 유효합니다.</strong>
                            </div>
                            <div class="button">
                                <a href="%s">이메일 인증하기</a>
                            </div>
                        </div>
                        <div class="footer">
                            본 메일은 발신 전용입니다.<br>
                            © 2025 Day Memory. All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """, userName, verificationLink);
    }

    /**
     * 비밀번호 재설정 HTML 생성
     */
    private String buildPasswordResetHtml(String userName, String resetLink) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); margin: 0; padding: 40px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 16px; padding: 40px; box-shadow: 0 10px 40px rgba(0,0,0,0.1); }
                        .header { text-align: center; padding-bottom: 30px; border-bottom: 2px solid #f0f0f0; }
                        .logo { font-size: 28px; font-weight: bold; background: linear-gradient(135deg, #667eea, #764ba2); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
                        .content { padding: 30px 0; }
                        .greeting { font-size: 20px; font-weight: 600; color: #333; margin-bottom: 20px; }
                        .message { font-size: 16px; color: #666; line-height: 1.6; margin-bottom: 30px; }
                        .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 12px; margin: 20px 0; font-size: 14px; color: #856404; }
                        .button { text-align: center; margin: 30px 0; }
                        .button a { display: inline-block; padding: 14px 40px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; border-radius: 8px; font-weight: 600; }
                        .footer { text-align: center; padding-top: 30px; border-top: 2px solid #f0f0f0; color: #999; font-size: 14px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <div class="logo">📅 Day Memory</div>
                        </div>
                        <div class="content">
                            <div class="greeting">안녕하세요, %s님!</div>
                            <div class="message">
                                비밀번호 재설정 요청을 받았습니다.<br>
                                아래 버튼을 클릭하여 새로운 비밀번호를 설정해주세요.
                            </div>
                            <div class="warning">
                                ⚠️ 비밀번호 재설정을 요청하지 않으셨다면 이 메일을 무시하셔도 됩니다.<br>
                                재설정 링크는 1시간 동안만 유효합니다.
                            </div>
                            <div class="button">
                                <a href="%s">비밀번호 재설정하기</a>
                            </div>
                        </div>
                        <div class="footer">
                            본 메일은 발신 전용입니다.<br>
                            © 2025 Day Memory. All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """, userName, resetLink);
    }
}
