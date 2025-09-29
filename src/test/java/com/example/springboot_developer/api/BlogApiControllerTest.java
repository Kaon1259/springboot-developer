package com.example.springboot_developer.api;

import com.example.springboot_developer.dto.ArticleDto;
import com.example.springboot_developer.entity.Article;
import com.example.springboot_developer.service.ArticleService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

//    @DisplayName("아티클 생성 및 조회 테스트")
//    @Test
//    void addArticle() throws Exception {
//        //given
//        //Service Call
//        ArticleDto dto = new ArticleDto(null, "article title", "article content", LocalDateTime.now());
//        ArticleDto saved = articleService.save(dto, null);
//
//        System.out.println("SAVED DATA : " + saved.getId());
//
//        //Get
//        String url = "/api/articles/" + saved.getId();
//        final ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
//                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
//
//          result.andExpect(status().isOk())
//                .andExpect((ResultMatcher) content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
//                .andExpect((ResultMatcher) jsonPath("$.id").value(saved.getId()))
//                .andExpect((ResultMatcher) jsonPath("$.title").value("article title"))
//                .andExpect((ResultMatcher) jsonPath("$.content").value("article content"));
//    }
//
//    @DisplayName("아티클 생성 및 조회 테스트")
//    @Test
//    void addArticle2() throws Exception {
//        //given
//        //Service Call
//        //ArticleDto dto = new ArticleDto(null, "article title", "article content");
//        ArticleDto dto = ArticleDto.builder()
//                .id(null)
//                .title("article title")
//                .content("article content")
//                .build();
//
//
//        String url = "/api/articles";
//        ResultActions result = mockMvc.perform(
//                post(url)
//                        .contentType(MediaType.APPLICATION_JSON)           // ← 헤더: 본문 타입
//                        .accept(MediaType.APPLICATION_JSON)                // ← 헤더: 응답 기대 타입
//                        .characterEncoding("UTF-8")                       // (선택) 한글 깨짐 방지
//                        .content(objectMapper.writeValueAsString(dto))     // ← 바디: JSON 문자열
//        );
//
//        result.andExpect(status().isCreated());
//        String response =  result.andReturn().getResponse().getContentAsString();
//        ArticleDto saved = objectMapper.readValue(response, ArticleDto.class);
//
//        //Get
//        url = "/api/articles/" + saved.getId();
//        final ResultActions result1 = mockMvc.perform(MockMvcRequestBuilders.get(url)
//                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
//
//        result1.andExpect(status().isOk())
//                .andExpect((ResultMatcher) content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
//                .andExpect((ResultMatcher) jsonPath("$.id").value(saved.getId()))
//                .andExpect((ResultMatcher) jsonPath("$.title").value("article title"))
//                .andExpect((ResultMatcher) jsonPath("$.content").value("article content"));
//    }
//
//    @DisplayName("모든 아티클 조회하기")
//    @Test
//    void getArticles() throws Exception {
//        String url = "/api/articles/all";
//        ResultActions result =  mockMvc.perform(
//                            MockMvcRequestBuilders.get(url)
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .accept(MediaType.APPLICATION_JSON)
//                                    .characterEncoding("UTF-8")
//        );
//
//        String title = "article title";
//        String content = "article content";
//
//        result.andExpect(status().isOk())
//                .andExpect((ResultMatcher) content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
//                .andExpect(jsonPath("$.length()" ).value(26))
//                .andExpect(jsonPath("$[0].title").value(title))
//                .andExpect(jsonPath("$[0].content").value(content))
//                .andExpect(jsonPath("$[1].title").value(title));
//
////        String responseJson =  result.andReturn().getResponse().getContentAsString();
//
////        JavaType type = objectMapper.getTypeFactory()
////                .constructCollectionType(List.class, ArticleDto.class);
////
////        List<ArticleDto> articles = objectMapper.readValue(responseJson, type);
////
////        assertThat(articles.size()).isEqualTo(26);
//    }
//
//    @DisplayName("ID로 아티클 조회하기")
//    @Test
//    void getArticlesById() throws Exception {
//        String url = "/api/articles/1";
//        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .characterEncoding("UTF-8"));
//
//        System.out.println(result.andReturn().getResponse().getContentAsString());
//        result.andExpect(status().isOk())
//                .andExpect((ResultMatcher) content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
//                .andExpect(jsonPath("$.size()" ).value(3))
//                .andExpect(jsonPath("$.title").value("article title"));
//    }
//
//    @DisplayName("아티클 수정하기")
//    @Test
//    void updateArticleById() throws Exception {
//        String title ="changed title";
//        String content ="changed content";
//        ArticleDto dto = ArticleDto.builder()
//                .id(2L)
//                .title(title)
//                .content(content)
//                .build();
//
//        System.out.println("dto : " + dto.getId() + " " + dto.getTitle() + " " + dto.getContent());
//
//        String url = "/api/articles/2";
//        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto))
//                .accept(MediaType.APPLICATION_JSON)
//        );
//
//        result.andExpect(status().isOk())
//                .andExpect((ResultMatcher) content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
//                .andExpect((ResultMatcher) jsonPath("$.id").value(dto.getId()));
//
//        ResultActions resut = mockMvc.perform(MockMvcRequestBuilders.get(url)
//                                    .accept(MediaType.APPLICATION_JSON)
//                                    .characterEncoding("UTF-8")
//                                    .accept(MediaType.APPLICATION_JSON));
//        resut.andExpect(status().isOk())
//                .andExpect((ResultMatcher) content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
//                .andExpect((ResultMatcher) jsonPath("$.id").value(dto.getId()))
//                .andExpect((ResultMatcher) jsonPath("$.title").value(title))
//                .andExpect((ResultMatcher) jsonPath("$.content").value(content));
//    }
}