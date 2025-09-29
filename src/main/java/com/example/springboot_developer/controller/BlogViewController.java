package com.example.springboot_developer.controller;

import com.example.springboot_developer.dto.ArticleDto;
import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.service.ArticleService;
import com.example.springboot_developer.service.UserService;
import com.example.springboot_developer.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BlogViewController {

    final ArticleService articleService;
    private final UserService userService;

    @GetMapping("/articles")
    public String articles(@AuthenticationPrincipal User me, Model model) {
        try{
            model.addAttribute("articles", articleService.findAll());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        //model.addAttribute("articles",articleService.findAll());
        return "articles/articleList";
    }

    @GetMapping("/articles/{id}")
    public String getArticle (@PathVariable Long id, Model model) {
        model.addAttribute("article", articleService.findById(id));
        return "articles/article";
    }

    @GetMapping("/articles/me")
    public String getArticle (@AuthenticationPrincipal OAuth2User principal, Model model) {

        if (principal == null) {
            log.info("getArticle:Princiapl me is null");
            return "redirect:/";
        }

        String email = principal.getAttribute("email");

        User user = userService.findByEmail(email);
        List<ArticleDto> articleDtoList = articleService.findByUserId(user.getId());

        model.addAttribute("articles", articleDtoList);

        return "articles/articleList";
    }

    @GetMapping("/new-article")
    public String newArticle (@RequestParam(required = false) Long id, Model model) {
        //id가 없으면 생성 없으면 수정
        if (id != null) {
            model.addAttribute("article", articleService.findById(id));
        }else {
            //빈객체를 넘긴다.
            model.addAttribute("article", new ArticleDto());
        }

        return "articles/newArticle";
    }
}
