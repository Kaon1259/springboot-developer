package com.example.springboot_developer.dto;

import com.example.springboot_developer.entity.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserDto {
    private String email;
    private String password;
    private String nickname;
    private String registrationid;

    public User toEntity(){
        return new User(this.email, this.password, this.nickname, this.registrationid);
    }

    public static UserDto fromEntity(User user){
        return new UserDto(user.getEmail(), user.getPassword(), user.getNickname(), user.getRegistrationId());
    }
}
