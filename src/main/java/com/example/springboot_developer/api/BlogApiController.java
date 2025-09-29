package com.example.springboot_developer.api;

import com.example.springboot_developer.dto.ArticleDto;
import com.example.springboot_developer.entity.Article;
import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.service.ArticleService;
import com.example.springboot_developer.service.MemberService;
import com.example.springboot_developer.service.UserService;
import com.example.springboot_developer.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BlogApiController {

    //@RequiredArgsConstructor에 의해서 생성자 호출로 생성된다.
    private final ArticleService articleService;
    private final MemberService memberService;
    private final UserService userService;

    //아래 생성자는 @RequiredArgsConstructor에 의해서 생성된다.
//    @Autowired
//    public BlogApiController(ArticleService articleService) {
//        this.articleService = articleService;
//    }

    //Save Article
    @PostMapping("/api/articles")
    public ResponseEntity<ArticleDto> addArticle(@RequestBody ArticleDto dto, Principal principal) {

        if(principal != null) {
            String email = AuthenticationUtil.getEmail(principal);

            if(!email.isEmpty()){
                User me = userService.findByEmail(email);
                if (me == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArticleDto());
                }

                ArticleDto saved = articleService.save(dto, me);
                return ResponseEntity.status(HttpStatus.CREATED).body(saved);
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArticleDto());
    }

    //findById
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleDto> getArticlesById(@PathVariable Long id) {
        ArticleDto dto = articleService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    //findByAll
    @GetMapping("/api/articles/all")
    public ResponseEntity<List<ArticleDto>> getArticles() {
        List<ArticleDto> dtos = articleService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    //delete article by id
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticleById(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //Update
    @PatchMapping("/api/articles/{id}")
    public ResponseEntity<ArticleDto> updateArticleById(@PathVariable Long id, @RequestBody ArticleDto dto) {
        log.info("Update article : " + id + " " + dto.getId());
        if(id.equals(dto.getId())) {
            log.info("In Updating article with id {}", id);
            ArticleDto saved = articleService.update(dto);
            return ResponseEntity.status(HttpStatus.OK).body(saved);
        }
        log.info("Out Updating article with id {}", dto.getId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
