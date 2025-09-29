package com.example.springboot_developer.dto;

import com.example.springboot_developer.entity.Article;
import com.example.springboot_developer.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ArticleDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    //@Builder 어노테이션으로 선언되 빌더 객체로 Entity 생성
    public Article toEntity() {
        return Article.builder()
                .title(this.title)
                .content(this.content)
                .build();
    }

    public Article toEntityWithId () {
        return Article.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .build();
    }

    public static Article toEntity(ArticleDto dto, User user) {
        return Article.builder()
                .id(dto.getId())
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public static ArticleDto fromEntity(Article article) {
        return ArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .createdAt(article.getCreatedAt())
                .build();
    }
}
