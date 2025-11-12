package com.daymemory.service;

import com.daymemory.domain.dto.UserDto;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.UserRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import com.daymemory.security.CustomUserDetails;
import com.daymemory.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입
     */
    @Transactional
    public UserDto.Response signup(UserDto.SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        User savedUser = userRepository.save(user);

        return UserDto.Response.from(savedUser);
    }

    /**
     * 로그인
     */
    public UserDto.LoginResponse login(UserDto.LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return UserDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserDto.Response.from(user))
                .build();
    }

    /**
     * 토큰 재발급
     */
    public UserDto.RefreshResponse refresh(UserDto.RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // Refresh Token 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());

        return UserDto.RefreshResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    /**
     * 내 정보 조회
     */
    public UserDto.Response getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserDto.Response.from(user);
    }

    /**
     * 내 정보 수정
     */
    @Transactional
    public UserDto.Response updateMyInfo(Long userId, UserDto.UpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 닉네임 수정
        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname());
        }

        // 프로필 이미지 URL 수정
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        return UserDto.Response.from(user);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(Long userId, UserDto.PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호로 변경
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(Long userId) {
        // 현재는 JWT를 사용하므로 클라이언트 측에서 토큰 삭제
        // 필요시 Redis 등을 사용하여 토큰 블랙리스트 구현 가능
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // 로그아웃 로직 (현재는 클라이언트 측에서 토큰 삭제로 처리)
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자 삭제
        userRepository.delete(user);
    }

    /**
     * 알림 설정 조회
     */
    public UserDto.NotificationSettingsResponse getNotificationSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재는 기본값 반환 (추후 User 엔티티에 필드 추가 필요)
        return UserDto.NotificationSettingsResponse.builder()
                .emailNotificationsEnabled(true)
                .reminderTime("09:00")
                .build();
    }

    /**
     * 알림 설정 수정
     */
    @Transactional
    public UserDto.NotificationSettingsResponse updateNotificationSettings(Long userId, UserDto.NotificationSettingsRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재는 요청값 그대로 반환 (추후 User 엔티티에 필드 추가하여 저장 필요)
        return UserDto.NotificationSettingsResponse.builder()
                .emailNotificationsEnabled(request.getEmailNotificationsEnabled())
                .reminderTime(request.getReminderTime())
                .build();
    }

    /**
     * 현재 로그인한 사용자 ID 가져오기
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId();
        }
        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}
