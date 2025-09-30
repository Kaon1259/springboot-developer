package com.example.springboot_developer.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.security.Principal;
import java.util.Map;

@Slf4j
public class AuthenticationUtil {

    @SuppressWarnings("unchecked")
    public static String extractEmail(String registrationId, OAuth2User principal) {
        Map<String, Object> attr = principal.getAttributes();

        switch (registrationId) {
            case "google":
                return (String) attr.get("email");
            case "naver":
                Map<String, Object> resp = (Map<String, Object>) attr.get("response");
                return resp != null ? (String) resp.get("email") : null;
            case "kakao":
                Map<String, Object> account = (Map<String, Object>) attr.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) account.get("profile");
                log.info("kakao:extractEmail:kakao_account:profile:nickname = {}", profile.get("nickname"));
                return profile != null ? (String) profile.get("nickname") + "@_kakao.com" : null;
            case "facebook":
                return (String) attr.get("email");
            default:
                return (String) attr.get("email"); // fallback
        }
    }

    @SuppressWarnings("unchecked")
    public static String extractNickname(String registrationId, OAuth2User principal) {
        Map<String, Object> attr = principal.getAttributes();

        switch (registrationId) {
            case "google":
                return (String) attr.get("email");
            case "facebook":
                return (String) attr.get("email");
            case "naver":
                Map<String, Object> resp = (Map<String, Object>) attr.get("response");
                return resp != null ? (String) resp.get("email") : null;
            case "kakao":
                Map<String, Object> account = (Map<String, Object>) attr.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) account.get("profile");
                log.info("kakao:extractEmail:kakao_account:profile:nickname = {}", profile.get("nickname"));
                return profile != null ? (String) profile.get("nickname")  : null;
            default:
                return (String) attr.get("email"); // fallback
        }
    }

    public static String getEmail(Principal principal) {

        //form login
        if (principal instanceof UsernamePasswordAuthenticationToken authToken
                && authToken.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof OAuth2AuthenticationToken oauthToken) {
            // OAuth2 Login
            String email = AuthenticationUtil.extractEmail(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getPrincipal());

            // provider마다 키가 다를 수 있으므로 필요시 분기
            return  email; //(String) oAuth2User.getAttributes().get("email");
        }

        return null;
    }
}
