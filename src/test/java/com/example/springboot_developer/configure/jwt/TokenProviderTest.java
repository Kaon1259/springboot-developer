package com.example.springboot_developer.configure.jwt;

import com.example.springboot_developer.dto.UserDto;
import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.repository.UserRepository;
import com.example.springboot_developer.service.UserService;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
class TokenProviderTest {

    private static final Logger log = LoggerFactory.getLogger(TokenProviderTest.class);

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserService service;

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserService userService;


//    @DisplayName("정상 토큰 테스트")
//    @Test
//    void generateToken() {
//
//        UserDto userDto = UserDto.builder().email("molly6@naver.com").password("1111").build();
//        User user =  userService.save(userDto);
//
//        log.info("user id is {}", user.getId());
//
//        String token = tokenProvider.generateToken(user, Duration.ofMinutes(5));
//
//        Long id = tokenProvider.getUserIdFromToken(token);
//
//        assertThat(id).isEqualTo(user.getId());
//    }
//
//    @DisplayName("만료 토큰 테스트")
//    @Test
//    void generateTokenDurationExpired() {
//
//        UserDto userDto = UserDto.builder().email("molly8@naver.com").password("1111").build();
//        User user =  userService.save(userDto);
//
//        String token = tokenProvider.generateToken(user, Duration.ofMinutes(4));
//
//        boolean  result = tokenProvider.verifyToken(token);
//
//        assertThat(result).isTrue();
//    }
//
//
//    @DisplayName("token으로 사용자 정보을 읽기")
//    @Test
//    void generateTokenAndGetAuthentication() {
//
//        UserDto userDto = UserDto.builder().email("molly9@naver.com").password("1111").build();
//        User user =  userService.save(userDto);
//
//        String token = tokenProvider.generateToken(user, Duration.ofMinutes(4));
//        boolean  result = tokenProvider.verifyToken(token);
//        if(result) {
//            Authentication authentication = tokenProvider.getAuthentication(token);
//
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            assertThat(userDetails.getUsername()).isEqualTo(userDto.getEmail());
//        }
//    }
}

