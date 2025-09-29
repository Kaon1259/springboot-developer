package com.example.springboot_developer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@Table(name="users")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long id;

    @Column(name="email", updatable = false, nullable = false)
    private String email;

    @Column(name ="password", updatable = true, nullable = false, unique = true)
    private String password;

    @Column(name="nickname", updatable = false, nullable = true)
    private String nickname;

    @CreatedDate
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="registration_id", updatable = true, nullable = false)
    private String registrationId;

    @Builder
    public User(String email, String password, String nickname, String registrationId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.registrationId = registrationId;
    }

    //권한 리스트 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    //사용자 아이디 반환
    @Override
    public String getUsername() {
        return email;
    }

    //비밀 번화 반환
    @Override
    public String getPassword() {
        return password;
    }

    //계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        //return UserDetails.super.isAccountNonExpired();
        return true; //true -> 계정 만료 되지 않음
    }

    //계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true; //true -> 계정 잠기지 않음
        //return UserDetails.super.isAccountNonLocked();
    }

    //패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;   //true -> 만료되지 않음
        //return UserDetails.super.isCredentialsNonExpired();
    }

    //계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        return true; //true -> 사용 가능
        //return UserDetails.super.isEnabled();
    }

    //nickname 변경
    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }
}
