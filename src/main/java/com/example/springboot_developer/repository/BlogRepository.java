package com.example.springboot_developer.repository;

import com.example.springboot_developer.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogRepository extends JpaRepository<Article, Long> {

    Page<Article> findAll(Pageable pageable);

    List<Article> findByTitle(String title);

    //JPLQuery
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:title%")
    List<Article> searchByTitleLike(@Param("title") String title);

    //Native Query
    @Query(value = "select a.* from article a where a.user_id = :userId", nativeQuery = true)
    List<Article> findByUserId(@Param("userId") Long userId);
}
