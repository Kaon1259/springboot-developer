package com.example.springboot_developer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CreateAccessTokenRequest {
    private String refreshToken;
}
