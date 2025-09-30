package com.example.springboot_developer.configure.jwt;

import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.util.CookieUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String ACCESS_TOKEN_COOKIE_NAME ="access_token";
    private final static String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //context filter에 인증 정보가 존재하면 이미 인증 받은 것으로 처리한다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication != null) && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            filterChain.doFilter(request, response);
            return; // 즉시 종료
        }

        String token = resolveFromAuthorizationHeader(request);
        if (token == null) {
            token = CookieUtil.getCookieValue(request, ACCESS_TOKEN_COOKIE_NAME); // ✅ 쿠키에서도 조회

            if(token == null) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        try {
            if (tokenProvider.verifyToken(token)) {
                Authentication auth = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                return;
            }
        } catch (JwtException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);     // ✅ 예외를 401로 고정
            return;
        }

       filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private String resolveFromCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) if (name.equals(c.getName())) return c.getValue();
        return null;
    }

    public static String resolveFromAuthorizationHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) return null;

        String prefix = BEARER_PREFIX ; //"Bearer ";
        if (header.regionMatches(true, 0, prefix, 0, prefix.length())) {
            String token = header.substring(prefix.length()).trim();
            return token.isEmpty() ? null : token;
        }
        return null;
    }
}
