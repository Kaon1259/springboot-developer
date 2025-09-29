package com.example.springboot_developer.api;

import com.example.springboot_developer.configure.jwt.JwtProperties;
import com.example.springboot_developer.configure.jwt.TokenProvider;
import com.example.springboot_developer.dto.CreateAccessTokenRequest;
import com.example.springboot_developer.entity.RefreshToken;
import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.repository.RefreshTokenRepository;
import com.example.springboot_developer.repository.UserRepository;
import com.example.springboot_developer.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class TokenApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserService userService;

//    @BeforeEach
//    void setUp() {
//        refreshTokenRepository.deleteAll();
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//    }
//
//    @DisplayName("새로운 Access Token을 발급한다.")
//    @Test
//    void createNewAccessToken() throws Exception {
//
//        //url
//        final String url = "/api/token";
//
//        //generate refresh token
//            User user = userService.findById(3L);
//            String refreshToken = tokenProvider.generateToken(user, Duration.ofHours(2));
//
//            //save refresh token
//            refreshTokenRepository.save(RefreshToken.builder().user_id(user.getId()).refreshToken(refreshToken).build());
//            CreateAccessTokenRequest createAccessTokenRequest = CreateAccessTokenRequest.builder().refreshToken(refreshToken).build();
//
//            final String requestBody = objectMapper.writeValueAsString(createAccessTokenRequest);
//
//            ResultActions resultActions = mockMvc.perform(post(url)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(requestBody)
//            );
//
//            resultActions.andExpect(status().isCreated())
//                    .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value(refreshToken));
//    }
}