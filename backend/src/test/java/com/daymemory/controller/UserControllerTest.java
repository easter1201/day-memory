package com.daymemory.controller;

import com.daymemory.config.SecurityConfig;
import com.daymemory.domain.dto.UserDto;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import com.daymemory.security.CustomUserDetailsService;
import com.daymemory.security.JwtAuthenticationFilter;
import com.daymemory.security.JwtTokenProvider;
import com.daymemory.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@DisplayName("UserController 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserDto.SignupRequest signupRequest;
    private UserDto.LoginRequest loginRequest;
    private UserDto.Response userResponse;
    private UserDto.LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        // Given: 회원가입 요청 DTO
        signupRequest = UserDto.SignupRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .build();

        // Given: 로그인 요청 DTO
        loginRequest = UserDto.LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // Given: 사용자 응답 DTO
        userResponse = UserDto.Response.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .build();

        // Given: 로그인 응답 DTO
        loginResponse = UserDto.LoginResponse.builder()
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .user(userResponse)
                .build();
    }

    @Test
    @DisplayName("POST /api/users/signup - 회원가입 성공")
    void testSignup_Success() throws Exception {
        // Given
        given(userService.signup(any(UserDto.SignupRequest.class)))
                .willReturn(userResponse);

        // When & Then
        mockMvc.perform(post("/api/users/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));

        // Verify
        then(userService).should(times(1)).signup(any(UserDto.SignupRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/signup - 유효성 검증 실패 (빈 이메일)")
    void testSignup_ValidationFailed_EmptyEmail() throws Exception {
        // Given
        UserDto.SignupRequest invalidRequest = UserDto.SignupRequest.builder()
                .email("")  // 빈 이메일
                .password("password123")
                .name("Test User")
                .build();

        // When & Then
        mockMvc.perform(post("/api/users/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(userService).should(never()).signup(any(UserDto.SignupRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/signup - 유효성 검증 실패 (잘못된 이메일 형식)")
    void testSignup_ValidationFailed_InvalidEmail() throws Exception {
        // Given
        UserDto.SignupRequest invalidRequest = UserDto.SignupRequest.builder()
                .email("invalid-email")  // 잘못된 이메일 형식
                .password("password123")
                .name("Test User")
                .build();

        // When & Then
        mockMvc.perform(post("/api/users/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(userService).should(never()).signup(any(UserDto.SignupRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/signup - 유효성 검증 실패 (짧은 비밀번호)")
    void testSignup_ValidationFailed_ShortPassword() throws Exception {
        // Given
        UserDto.SignupRequest invalidRequest = UserDto.SignupRequest.builder()
                .email("test@example.com")
                .password("short")  // 8자 미만
                .name("Test User")
                .build();

        // When & Then
        mockMvc.perform(post("/api/users/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(userService).should(never()).signup(any(UserDto.SignupRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/signup - 이메일 중복")
    void testSignup_DuplicateEmail() throws Exception {
        // Given
        given(userService.signup(any(UserDto.SignupRequest.class)))
                .willThrow(new CustomException(ErrorCode.USER_ALREADY_EXISTS));

        // When & Then
        mockMvc.perform(post("/api/users/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isConflict());

        // Verify
        then(userService).should(times(1)).signup(any(UserDto.SignupRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/login - 로그인 성공")
    void testLogin_Success() throws Exception {
        // Given
        given(userService.login(any(UserDto.LoginRequest.class)))
                .willReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/api/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("test-refresh-token"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));

        // Verify
        then(userService).should(times(1)).login(any(UserDto.LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/login - 유효성 검증 실패 (빈 비밀번호)")
    void testLogin_ValidationFailed_EmptyPassword() throws Exception {
        // Given
        UserDto.LoginRequest invalidRequest = UserDto.LoginRequest.builder()
                .email("test@example.com")
                .password("")  // 빈 비밀번호
                .build();

        // When & Then
        mockMvc.perform(post("/api/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(userService).should(never()).login(any(UserDto.LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/login - 잘못된 인증 정보")
    void testLogin_InvalidCredentials() throws Exception {
        // Given
        given(userService.login(any(UserDto.LoginRequest.class)))
                .willThrow(new CustomException(ErrorCode.INVALID_PASSWORD));

        // When & Then
        mockMvc.perform(post("/api/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Verify
        then(userService).should(times(1)).login(any(UserDto.LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/refresh - 토큰 재발급 성공")
    void testRefresh_Success() throws Exception {
        // Given
        UserDto.RefreshRequest refreshRequest = UserDto.RefreshRequest.builder()
                .refreshToken("test-refresh-token")
                .build();

        UserDto.RefreshResponse refreshResponse = UserDto.RefreshResponse.builder()
                .accessToken("new-access-token")
                .build();

        given(userService.refresh(any(UserDto.RefreshRequest.class)))
                .willReturn(refreshResponse);

        // When & Then
        mockMvc.perform(post("/api/users/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));

        // Verify
        then(userService).should(times(1)).refresh(any(UserDto.RefreshRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/refresh - 유효성 검증 실패 (빈 리프레시 토큰)")
    void testRefresh_ValidationFailed_EmptyToken() throws Exception {
        // Given
        UserDto.RefreshRequest invalidRequest = UserDto.RefreshRequest.builder()
                .refreshToken("")  // 빈 리프레시 토큰
                .build();

        // When & Then
        mockMvc.perform(post("/api/users/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(userService).should(never()).refresh(any(UserDto.RefreshRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/refresh - 유효하지 않은 리프레시 토큰")
    void testRefresh_InvalidToken() throws Exception {
        // Given
        UserDto.RefreshRequest refreshRequest = UserDto.RefreshRequest.builder()
                .refreshToken("invalid-refresh-token")
                .build();

        given(userService.refresh(any(UserDto.RefreshRequest.class)))
                .willThrow(new CustomException(ErrorCode.INVALID_TOKEN));

        // When & Then
        mockMvc.perform(post("/api/users/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Verify
        then(userService).should(times(1)).refresh(any(UserDto.RefreshRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/users/me - 내 정보 조회 성공")
    void testGetMyInfo_Success() throws Exception {
        // Given
        given(userService.getCurrentUserId()).willReturn(1L);
        given(userService.getMyInfo(1L)).willReturn(userResponse);

        // When & Then
        mockMvc.perform(get("/api/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));

        // Verify
        then(userService).should(times(1)).getCurrentUserId();
        then(userService).should(times(1)).getMyInfo(1L);
    }

    @Test
    @DisplayName("GET /api/users/me - 인증 없이 접근 시도 (401 Unauthorized)")
    void testGetMyInfo_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Verify
        then(userService).should(never()).getCurrentUserId();
        then(userService).should(never()).getMyInfo(anyLong());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/users/me - 사용자를 찾을 수 없음")
    void testGetMyInfo_UserNotFound() throws Exception {
        // Given
        given(userService.getCurrentUserId()).willReturn(999L);
        given(userService.getMyInfo(999L))
                .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        // When & Then
        mockMvc.perform(get("/api/users/me"))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(userService).should(times(1)).getCurrentUserId();
        then(userService).should(times(1)).getMyInfo(999L);
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/users/me - 내 정보 수정 성공")
    void testUpdateMyInfo_Success() throws Exception {
        // Given
        UserDto.UpdateRequest updateRequest = UserDto.UpdateRequest.builder()
                .name("Updated Name")
                .build();

        UserDto.Response updatedResponse = UserDto.Response.builder()
                .id(1L)
                .email("test@example.com")
                .name("Updated Name")
                .build();

        given(userService.getCurrentUserId()).willReturn(1L);
        given(userService.updateMyInfo(eq(1L), any(UserDto.UpdateRequest.class)))
                .willReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"));

        // Verify
        then(userService).should(times(1)).getCurrentUserId();
        then(userService).should(times(1)).updateMyInfo(eq(1L), any(UserDto.UpdateRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/users/me - 유효성 검증 실패 (이름 길이 초과)")
    void testUpdateMyInfo_ValidationFailed_NameTooLong() throws Exception {
        // Given
        UserDto.UpdateRequest invalidRequest = UserDto.UpdateRequest.builder()
                .name("A".repeat(101))  // 100자 초과
                .build();

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(userService).should(never()).updateMyInfo(anyLong(), any(UserDto.UpdateRequest.class));
    }

    @Test
    @DisplayName("PUT /api/users/me - 인증 없이 접근 시도 (401 Unauthorized)")
    void testUpdateMyInfo_Unauthorized() throws Exception {
        // Given
        UserDto.UpdateRequest updateRequest = UserDto.UpdateRequest.builder()
                .name("Updated Name")
                .build();

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Verify
        then(userService).should(never()).getCurrentUserId();
        then(userService).should(never()).updateMyInfo(anyLong(), any(UserDto.UpdateRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/users/password - 비밀번호 변경 성공")
    void testChangePassword_Success() throws Exception {
        // Given
        UserDto.PasswordChangeRequest passwordRequest = UserDto.PasswordChangeRequest.builder()
                .currentPassword("oldpassword123")
                .newPassword("newpassword456")
                .build();

        given(userService.getCurrentUserId()).willReturn(1L);
        willDoNothing().given(userService).changePassword(eq(1L), any(UserDto.PasswordChangeRequest.class));

        // When & Then
        mockMvc.perform(put("/api/users/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify
        then(userService).should(times(1)).getCurrentUserId();
        then(userService).should(times(1)).changePassword(eq(1L), any(UserDto.PasswordChangeRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/users/password - 유효성 검증 실패 (짧은 새 비밀번호)")
    void testChangePassword_ValidationFailed_ShortNewPassword() throws Exception {
        // Given
        UserDto.PasswordChangeRequest invalidRequest = UserDto.PasswordChangeRequest.builder()
                .currentPassword("oldpassword123")
                .newPassword("short")  // 8자 미만
                .build();

        // When & Then
        mockMvc.perform(put("/api/users/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(userService).should(never()).changePassword(anyLong(), any(UserDto.PasswordChangeRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/users/password - 현재 비밀번호 불일치")
    void testChangePassword_WrongCurrentPassword() throws Exception {
        // Given
        UserDto.PasswordChangeRequest passwordRequest = UserDto.PasswordChangeRequest.builder()
                .currentPassword("wrongpassword")
                .newPassword("newpassword456")
                .build();

        given(userService.getCurrentUserId()).willReturn(1L);
        willThrow(new CustomException(ErrorCode.INVALID_PASSWORD))
                .given(userService).changePassword(eq(1L), any(UserDto.PasswordChangeRequest.class));

        // When & Then
        mockMvc.perform(put("/api/users/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(userService).should(times(1)).getCurrentUserId();
        then(userService).should(times(1)).changePassword(eq(1L), any(UserDto.PasswordChangeRequest.class));
    }

    @Test
    @DisplayName("PUT /api/users/password - 인증 없이 접근 시도 (401 Unauthorized)")
    void testChangePassword_Unauthorized() throws Exception {
        // Given
        UserDto.PasswordChangeRequest passwordRequest = UserDto.PasswordChangeRequest.builder()
                .currentPassword("oldpassword123")
                .newPassword("newpassword456")
                .build();

        // When & Then
        mockMvc.perform(put("/api/users/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Verify
        then(userService).should(never()).getCurrentUserId();
        then(userService).should(never()).changePassword(anyLong(), any(UserDto.PasswordChangeRequest.class));
    }
}
