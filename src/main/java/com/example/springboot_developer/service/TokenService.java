package com.example.springboot_developer.service;

import com.example.springboot_developer.configure.jwt.TokenProvider;
import com.example.springboot_developer.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    //@AutoWired... 실행  final / static의 경우 @RequiredArgsConstructor
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        //1. token 유효성 검사
        //2. 유효하지 않은 토큰인 경우 예외를 던진다.
        if(!tokenProvider.verifyToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
