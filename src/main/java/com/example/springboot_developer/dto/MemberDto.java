package com.example.springboot_developer.dto;

import com.example.springboot_developer.entity.Member;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class MemberDto {
    private Long id;
    private String name;
    private int age;

    public Member toEntity() {
        return new Member(null, name, age);
    }
    public static Member toEntity(MemberDto dto) {
        return new Member(dto.getId(), dto.getName(), dto.getAge());
    }
}
