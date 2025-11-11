package com.daymemory.service;

import com.daymemory.domain.dto.UserDto;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.UserRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import com.daymemory.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto.SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 사용자 설정
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword123")
                .name("테스트 사용자")
                .emailVerified(false)
                .build();

        // Given: 회원가입 요청 DTO
        signupRequest = UserDto.SignupRequest.builder()
                .email("newuser@example.com")
                .password("password123")
                .name("새 사용자")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void testSignup_Success() {
        // Given
        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);
        given(passwordEncoder.encode(signupRequest.getPassword())).willReturn("encodedPassword");

        User savedUser = User.builder()
                .id(1L)
                .email(signupRequest.getEmail())
                .password("encodedPassword")
                .name(signupRequest.getName())
                .emailVerified(false)
                .build();

        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // When
        UserDto.Response response = userService.signup(signupRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(signupRequest.getEmail());
        assertThat(response.getName()).isEqualTo(signupRequest.getName());

        // Verify
        then(userRepository).should(times(1)).existsByEmail(signupRequest.getEmail());
        then(passwordEncoder).should(times(1)).encode(signupRequest.getPassword());
        then(userRepository).should(times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void testSignup_DuplicateEmail() {
        // Given
        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.signup(signupRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_ALREADY_EXISTS);

        // Verify
        then(passwordEncoder).should(never()).encode(anyString());
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void testLogin_Success() {
        // Given
        UserDto.LoginRequest loginRequest = UserDto.LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).willReturn(true);

        Authentication authentication = mock(Authentication.class);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);

        String accessToken = "access.token.here";
        String refreshToken = "refresh.token.here";
        given(jwtTokenProvider.generateAccessToken(testUser.getId(), testUser.getEmail()))
                .willReturn(accessToken);
        given(jwtTokenProvider.generateRefreshToken(testUser.getId()))
                .willReturn(refreshToken);

        // When
        UserDto.LoginResponse response = userService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(response.getUser().getEmail()).isEqualTo(testUser.getEmail());

        // Verify
        then(userRepository).should(times(1)).findByEmail(loginRequest.getEmail());
        then(passwordEncoder).should(times(1)).matches(loginRequest.getPassword(), testUser.getPassword());
        then(authenticationManager).should(times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void testLogin_UserNotFound() {
        // Given
        UserDto.LoginRequest loginRequest = UserDto.LoginRequest.builder()
                .email("nonexistent@example.com")
                .password("password123")
                .build();

        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        // Verify
        then(passwordEncoder).should(never()).matches(anyString(), anyString());
        then(authenticationManager).should(never()).authenticate(any());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void testLogin_InvalidPassword() {
        // Given
        UserDto.LoginRequest loginRequest = UserDto.LoginRequest.builder()
                .email("test@example.com")
                .password("wrongpassword")
                .build();

        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);

        // Verify
        then(authenticationManager).should(never()).authenticate(any());
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void testRefresh_Success() {
        // Given
        String refreshToken = "valid.refresh.token";
        UserDto.RefreshRequest refreshRequest = UserDto.RefreshRequest.builder()
                .refreshToken(refreshToken)
                .build();

        given(jwtTokenProvider.validateRefreshToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getUserIdFromToken(refreshToken)).willReturn(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

        String newAccessToken = "new.access.token";
        given(jwtTokenProvider.generateAccessToken(testUser.getId(), testUser.getEmail()))
                .willReturn(newAccessToken);

        // When
        UserDto.RefreshResponse response = userService.refresh(refreshRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);

        // Verify
        then(jwtTokenProvider).should(times(1)).validateRefreshToken(refreshToken);
        then(jwtTokenProvider).should(times(1)).getUserIdFromToken(refreshToken);
        then(userRepository).should(times(1)).findById(1L);
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 유효하지 않은 토큰")
    void testRefresh_InvalidToken() {
        // Given
        String invalidToken = "invalid.token";
        UserDto.RefreshRequest refreshRequest = UserDto.RefreshRequest.builder()
                .refreshToken(invalidToken)
                .build();

        given(jwtTokenProvider.validateRefreshToken(invalidToken)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.refresh(refreshRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);

        // Verify
        then(jwtTokenProvider).should(never()).getUserIdFromToken(anyString());
        then(userRepository).should(never()).findById(any());
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void testGetMyInfo_Success() {
        // Given
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

        // When
        UserDto.Response response = userService.getMyInfo(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getName()).isEqualTo(testUser.getName());

        // Verify
        then(userRepository).should(times(1)).findById(1L);
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 존재하지 않는 사용자")
    void testGetMyInfo_UserNotFound() {
        // Given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getMyInfo(999L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("내 정보 수정 성공")
    void testUpdateMyInfo_Success() {
        // Given
        UserDto.UpdateRequest updateRequest = UserDto.UpdateRequest.builder()
                .name("수정된 이름")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

        // When
        UserDto.Response response = userService.updateMyInfo(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();

        // Verify
        then(userRepository).should(times(1)).findById(1L);
    }

    @Test
    @DisplayName("내 정보 수정 - 빈 이름 무시")
    void testUpdateMyInfo_IgnoreBlankName() {
        // Given
        UserDto.UpdateRequest updateRequest = UserDto.UpdateRequest.builder()
                .name("   ")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

        // When
        UserDto.Response response = userService.updateMyInfo(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();
        // 이름이 변경되지 않아야 함

        // Verify
        then(userRepository).should(times(1)).findById(1L);
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void testChangePassword_Success() {
        // Given
        UserDto.PasswordChangeRequest passwordChangeRequest = UserDto.PasswordChangeRequest.builder()
                .currentPassword("password123")
                .newPassword("newPassword456")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(passwordChangeRequest.getCurrentPassword(), testUser.getPassword()))
                .willReturn(true);
        given(passwordEncoder.encode(passwordChangeRequest.getNewPassword()))
                .willReturn("encodedNewPassword");

        // When
        userService.changePassword(1L, passwordChangeRequest);

        // Then
        // Verify
        then(userRepository).should(times(1)).findById(1L);
        then(passwordEncoder).should(times(1)).matches(
                passwordChangeRequest.getCurrentPassword(),
                testUser.getPassword()
        );
        then(passwordEncoder).should(times(1)).encode(passwordChangeRequest.getNewPassword());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
    void testChangePassword_InvalidCurrentPassword() {
        // Given
        UserDto.PasswordChangeRequest passwordChangeRequest = UserDto.PasswordChangeRequest.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword456")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(passwordChangeRequest.getCurrentPassword(), testUser.getPassword()))
                .willReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(1L, passwordChangeRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);

        // Verify
        then(passwordEncoder).should(never()).encode(anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 존재하지 않는 사용자")
    void testChangePassword_UserNotFound() {
        // Given
        UserDto.PasswordChangeRequest passwordChangeRequest = UserDto.PasswordChangeRequest.builder()
                .currentPassword("password123")
                .newPassword("newPassword456")
                .build();

        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(999L, passwordChangeRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 사용자 없음")
    void testRefresh_UserNotFoundAfterTokenValidation() {
        // Given
        String refreshToken = "valid.refresh.token";
        UserDto.RefreshRequest refreshRequest = UserDto.RefreshRequest.builder()
                .refreshToken(refreshToken)
                .build();

        given(jwtTokenProvider.validateRefreshToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getUserIdFromToken(refreshToken)).willReturn(999L);
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.refresh(refreshRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }
}
