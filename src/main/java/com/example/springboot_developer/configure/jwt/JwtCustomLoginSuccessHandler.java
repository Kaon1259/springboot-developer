package com.example.springboot_developer.configure.jwt;

import com.example.springboot_developer.configure.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.springboot_developer.entity.RefreshToken;
import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.repository.RefreshTokenRepository;
import com.example.springboot_developer.service.UserService;
import com.example.springboot_developer.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@Slf4j
public class JwtCustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";
    public final static String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri"; // 추가

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();

        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);

        saveRefreshToken(user.getId(), refreshToken);

        //addRefreshTokenToCookie(request, response, refreshToken);
        addHttpOnlyCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken,
                (int) REFRESH_TOKEN_DURATION.toSeconds(), request.isSecure());

        // ✅ 액세스 토큰도 반드시 HttpOnly 쿠키로 내려주세요
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        addHttpOnlyCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken,
                (int) ACCESS_TOKEN_DURATION.toSeconds(), request.isSecure());

        String targetUrl = getTargetUrl(request);

        clearAuthenticationAttributes(request, response);

        response.sendRedirect(targetUrl);
    }

    private void saveRefreshToken(Long userId, String newRefreshToken) {

    //    log.info("Step 5. JwtCustomLoginSuccessHandler:saveRefreshToken : "  + userId + " : "  + newRefreshToken);

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
   //     log.info("Step 6. JwtCustomLoginSuccessHandler:addRefreshTokenToCookie : " + refreshToken);

        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME);
    }

    private String getTargetUrl(HttpServletRequest request, String token) {
        // 1. 쿠키에서 저장된 원래 페이지 주소를 읽어옵니다.
        String targetUrl = CookieUtil.getCookie(request, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(REDIRECT_PATH); // 쿠키가 없으면 기본 경로(REDIRECT_PATH) 사용

        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    private String getTargetUrl(HttpServletRequest request) {
        String targetUrl = CookieUtil.getCookie(
                        request,
                        OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME
                ).map(Cookie::getValue)
                .orElse(REDIRECT_PATH); // 예: "/articles"
        return UriComponentsBuilder.fromUriString(targetUrl)
                .build()
                .toUriString();
    }

    private void addHttpOnlyCookie(HttpServletResponse response,
                                   String name, String value, int maxAgeSeconds, boolean secure) {
        // SameSite=Lax: 동일 도메인 내 네비게이션엔 전송, CSRF 리스크 낮음
        // HTTPS 환경에선 secure=true 권장
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
