package com.example.springboot_developer.service;

import com.example.springboot_developer.dto.ArticleDto;
import com.example.springboot_developer.entity.Article;
import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.repository.BlogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//@RequiredArgsConstructor
@Slf4j
@Service
public class ArticleService {

    private  final BlogRepository blogRepository;

    //DI 혹은 @RequiredArgsConstructor  --- final or @NotNull varialbes를 갖는 생성자를 빈드로 등록
    @Autowired
    public ArticleService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public ArticleDto save(ArticleDto dto, User me) {
        Article article = blogRepository.save(ArticleDto.toEntity(dto, me));
        return ArticleDto.fromEntity(article);
    }

    public List<ArticleDto> findAll() {

        try{
            Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
            log.info("pageable : " + pageable.toString());

            Page<Article> page = blogRepository.findAll(pageable);

//            List<ArticleDto> articleDtoList =  page.getContent().stream().map(ArticleDto::fromEntity).collect(Collectors.toList());
////
//            articleDtoList.forEach(articleDto -> {
//                log.info("articleDto : " + articleDto.getTitle() + " : " + articleDto.getCreatedAt());
//            });

            return page.getContent().stream().map(ArticleDto::fromEntity).collect(Collectors.toList());
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    public ArticleDto findById(Long id) {
        return blogRepository.findById(id).map(ArticleDto::fromEntity).orElse(null);
    }

    //JpaRepository에 정의해야 함  -- 도메인별 쿼리는 정의해야 함
    public List<ArticleDto> findByUserId(Long userId) {
        return blogRepository.findByUserId(userId).stream().map(ArticleDto::fromEntity).collect(Collectors.toList());
    }

    //JpaRepository에 정의해야 함  -- 도메인별 쿼리는 정의해야 함
    public List<ArticleDto> findByTitle(String title) {
        return blogRepository.findByTitle(title).stream().map(ArticleDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        if (blogRepository.existsById(id)) {
            blogRepository.deleteById(id);
        }
    }

    @Transactional
    public void delete(Article article) {
        if (blogRepository.existsById(article.getId())) {
            blogRepository.delete(article);
        }
    }

    //JpaRepository에 정의 해야 함
    public List<ArticleDto> findByTitleLike(String title) {
        return blogRepository.searchByTitleLike(title).stream().map(ArticleDto::fromEntity).collect(Collectors.toList());
    }

    public ArticleDto update(ArticleDto dto) {
        return blogRepository.findById(dto.getId()).map(
                article -> {
                        article.patch(dto.toEntityWithId());
                        Article updatedArticle = blogRepository.save(article);
                        return ArticleDto.fromEntity(updatedArticle);
                    }
                ).orElse(null);
    }
}
