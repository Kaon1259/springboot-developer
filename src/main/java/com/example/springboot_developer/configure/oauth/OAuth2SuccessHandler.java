package com.example.springboot_developer.configure.oauth;

import com.example.springboot_developer.configure.jwt.TokenProvider;
import com.example.springboot_developer.dto.UserDto;
import com.example.springboot_developer.entity.RefreshToken;
import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.repository.RefreshTokenRepository;
import com.example.springboot_developer.service.UserService;
import com.example.springboot_developer.util.CookieUtil;
import com.example.springboot_developer.util.AuthenticationUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import static com.example.springboot_developer.util.AuthenticationUtil.extractEmail;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";
    public static final String REDIRECT_SIGNUP_PATH = "/signup";
    public final static String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri"; // 추가

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        if( authentication instanceof OAuth2AuthenticationToken oAuth2User) {
            String email = extractEmail(oAuth2User.getAuthorizedClientRegistrationId(), oAuth2User.getPrincipal());

            if(email != null) {
                String registartionId = oAuth2User.getAuthorizedClientRegistrationId();
                log.info("registrationId : {} email: {}", registartionId, email);

                //User user = userService.findByEmailAndRegistrationId(email, registartionId);
                User user = userService.findByEmail(email);
                if(user == null){
                    log.info("USER NOT FOUND : {}", email);
                    user = createOauthAccount(oAuth2User, registartionId, email);
                }

                if(user != null) {
                    String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);

                    saveRefreshToken(user.getId(), refreshToken);

                    addRefreshTokenToCookie(request, response, refreshToken);
                    addHttpOnlyCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken,
                            (int) REFRESH_TOKEN_DURATION.toSeconds(), request.isSecure());

                    // ✅ 액세스 토큰도 반드시 HttpOnly 쿠키로 내려주세요
                    String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
                    addHttpOnlyCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken,
                            (int) ACCESS_TOKEN_DURATION.toSeconds(), request.isSecure());

                    clearAuthenticationAttributes(request, response);

                    String targetUrl = getTargetUrl(request);
                    getRedirectStrategy().sendRedirect(request, response, targetUrl);
                }
                else{
                    //사용자 계정으로 가입 시킨다.

                    String targetUrl = getSignupUrl(request);
                    getRedirectStrategy().sendRedirect(request, response, targetUrl);
                }
            }
        }
        else{
            log.info("authentication failed : {}", authentication);
        }
    }

    private void saveRefreshToken(Long userId, String newRefreshToken) {
    RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        CookieUtil.deleteCookie(request, response, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME);
    }

    private String getTargetUrl(HttpServletRequest request, String token) {
        // 1. 쿠키에서 저장된 원래 페이지 주소를 읽어옵니다.
        String targetUrl = CookieUtil.getCookie(request, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(REDIRECT_PATH); // 쿠키가 없으면 기본 경로(REDIRECT_PATH) 사용

        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
               // .queryParam("token", token)
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

    private String getSignupUrl(HttpServletRequest request) {
        String targetUrl = CookieUtil.getCookie(
                        request,
                        OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME
                ).map(Cookie::getValue)
                .orElse(REDIRECT_SIGNUP_PATH); // 예: "/articles"
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

    private  User createOauthAccount(OAuth2AuthenticationToken oAuth2User) {
        String registrationId = oAuth2User.getAuthorizedClientRegistrationId();
        String email = AuthenticationUtil.extractEmail(registrationId, oAuth2User.getPrincipal());

        log.info("createOauthAccount:registrationId : {} email: {}", registrationId, email);
        log.info("createOauthAccount:oAuth2User: {}", oAuth2User.toString());

        if(email != null) {
            UserDto dto = null;
            switch(registrationId) {
                case "google":
                    return userService.save(new UserDto(email, email, null, registrationId));
                case "naver":
                    return userService.save(new UserDto(email, email, null, registrationId));
                case "kakao":
                    log.info("createOauthAccount:registrationId:kakao : {} email: {}", registrationId, email);
                    String nickname = AuthenticationUtil.extractNickname(registrationId, oAuth2User.getPrincipal());
                    return userService.save(new UserDto(email, email, nickname, registrationId));
                default:
                    return null;
            }
        }
        return null;
    }

    private  User createOauthAccount(OAuth2AuthenticationToken oAuth2User, String registrationId, String email) {
        log.info("createOauthAccount:registrationId : {} email: {}", registrationId, email);

        if(email != null) {
            UserDto dto = null;
            switch(registrationId) {
                case "google":
                    return userService.save(new UserDto(email, email, null, registrationId));
                case "naver":
                    return userService.save(new UserDto(email, email, null, registrationId));
                case "kakao":
                    log.info("createOauthAccount:registrationId:kakao : {} email: {}", registrationId, email);
                    String nickname = AuthenticationUtil.extractNickname(registrationId, oAuth2User.getPrincipal());
                    return userService.save(new UserDto(email, email, nickname, registrationId));
                default:
                    return null;
            }
        }
        return null;
    }
}