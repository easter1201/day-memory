package com.daymemory.service;

import com.daymemory.domain.dto.OAuthDto;
import com.daymemory.domain.entity.OAuthProvider;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.UserRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import com.daymemory.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${oauth.google.client-id:}")
    private String googleClientId;

    @Value("${oauth.google.client-secret:}")
    private String googleClientSecret;

    @Value("${oauth.kakao.client-id:}")
    private String kakaoClientId;

    @Value("${oauth.kakao.client-secret:}")
    private String kakaoClientSecret;

    /**
     * Google OAuth 로그인
     */
    @Transactional
    public OAuthDto.OAuthLoginResponse googleLogin(OAuthDto.OAuthLoginRequest request) {
        // 1. Authorization code로 access token 획득
        String accessToken = getGoogleAccessToken(request.getCode(), request.getRedirectUri());

        // 2. Access token으로 사용자 정보 조회
        OAuthDto.OAuthUserInfo userInfo = getGoogleUserInfo(accessToken);

        // 3. 사용자 조회 또는 생성
        return processOAuthLogin(userInfo);
    }

    /**
     * Kakao OAuth 로그인
     */
    @Transactional
    public OAuthDto.OAuthLoginResponse kakaoLogin(OAuthDto.OAuthLoginRequest request) {
        // 1. Authorization code로 access token 획득
        String accessToken = getKakaoAccessToken(request.getCode(), request.getRedirectUri());

        // 2. Access token으로 사용자 정보 조회
        OAuthDto.OAuthUserInfo userInfo = getKakaoUserInfo(accessToken);

        // 3. 사용자 조회 또는 생성
        return processOAuthLogin(userInfo);
    }

    /**
     * Google Access Token 획득
     */
    private String getGoogleAccessToken(String code, String redirectUri) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, entity, String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            log.error("Failed to get Google access token", e);
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    /**
     * Google 사용자 정보 조회
     */
    private OAuthDto.OAuthUserInfo getGoogleUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, entity, String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            return OAuthDto.OAuthUserInfo.builder()
                    .providerId(jsonNode.get("id").asText())
                    .email(jsonNode.get("email").asText())
                    .name(jsonNode.get("name").asText())
                    .provider(OAuthProvider.GOOGLE)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get Google user info", e);
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    /**
     * Kakao Access Token 획득
     */
    private String getKakaoAccessToken(String code, String redirectUri) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoClientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, entity, String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            log.error("Failed to get Kakao access token", e);
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    /**
     * Kakao 사용자 정보 조회
     */
    private OAuthDto.OAuthUserInfo getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, entity, String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode kakaoAccount = jsonNode.get("kakao_account");
            JsonNode profile = kakaoAccount.get("profile");

            return OAuthDto.OAuthUserInfo.builder()
                    .providerId(jsonNode.get("id").asText())
                    .email(kakaoAccount.get("email").asText())
                    .name(profile.get("nickname").asText())
                    .provider(OAuthProvider.KAKAO)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get Kakao user info", e);
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    /**
     * OAuth 로그인 처리 (사용자 조회/생성 및 JWT 발급)
     */
    private OAuthDto.OAuthLoginResponse processOAuthLogin(OAuthDto.OAuthUserInfo userInfo) {
        // 1. OAuth Provider ID로 기존 사용자 조회
        Optional<User> existingUser = userRepository.findByOauthProviderAndOauthProviderId(
                userInfo.getProvider(), userInfo.getProviderId()
        );

        User user;
        boolean isNewUser = false;

        if (existingUser.isPresent()) {
            // 기존 사용자
            user = existingUser.get();
        } else {
            // 새로운 사용자 생성
            user = User.builder()
                    .email(userInfo.getEmail())
                    .nickname(userInfo.getName())
                    .password("OAUTH_USER_NO_PASSWORD")  // OAuth 사용자는 비밀번호 불필요
                    .oauthProvider(userInfo.getProvider())
                    .oauthProviderId(userInfo.getProviderId())
                    .emailVerified(true)  // OAuth 사용자는 이메일 자동 인증
                    .build();

            user = userRepository.save(user);
            isNewUser = true;
        }

        // 2. JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return OAuthDto.OAuthLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getNickname())
                .isNewUser(isNewUser)
                .build();
    }
}
