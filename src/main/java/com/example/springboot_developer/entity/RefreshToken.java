package com.example.springboot_developer.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name="refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", updatable = false)
    private Long id;

    @Column(name="user_id", nullable = false, unique=true)
    private Long userId;

    @Column(name="refresh_token", nullable = false)
    private String refreshToken;

    @Builder
    public RefreshToken(Long user_id, String refreshToken) {
        this.userId = user_id;
        this.refreshToken = refreshToken;
    }

    public RefreshToken  update(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
