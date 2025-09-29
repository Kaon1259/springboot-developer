package com.example.springboot_developer.configure.jwt;

import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.service.RefreshTokenService;
import com.example.springboot_developer.service.UserService;
import com.example.springboot_developer.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;

import static com.example.springboot_developer.util.AuthenticationUtil.extractEmail;

@Slf4j
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    final private UserService userService;
    final private RefreshTokenService refreshTokenService;
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        if (authentication != null) {
            deleteRefreshToken(authentication);
            deleteToken(request, response);
        }
    }

    private void deleteRefreshToken(Authentication authentication){
        if(authentication instanceof OAuth2AuthenticationToken oAuth2User) {
            String email = extractEmail(oAuth2User.getAuthorizedClientRegistrationId(), oAuth2User.getPrincipal());

            refreshTokenService.delete(email);
        }
    }

    private void deleteToken(HttpServletRequest request, HttpServletResponse response){
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_COOKIE_NAME);
    }
}
