package com.example.springboot_developer.service;

import com.example.springboot_developer.configure.jwt.TokenProvider;
import com.example.springboot_developer.entity.RefreshToken;
import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.repository.RefreshTokenRepository;
import com.example.springboot_developer.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final UserService userService;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken).orElse(null);
    }

    @Transactional
    public void delete() {

        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        if (token != null) {
            Long id = tokenProvider.getUserIdFromToken(token);
            refreshTokenRepository.deleteById(id);
            return;
        }

        //SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Transactional
    public void delete(String email) {

        User user = userService.findByEmail(email);

        if (user != null) {
            refreshTokenRepository.deleteByUserId(user.getId());
        }
    }

    @Transactional
    public void delete(Long userId) {
        if (userId != null) {
            refreshTokenRepository.deleteByUserId(userId);
        }
    }
}
