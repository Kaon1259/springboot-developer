package com.example.springboot_developer.repository;

import com.example.springboot_developer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);    //id를 제외한 나머지 컬럼의 조회의 findByXXX 형태로 정의해 주어야 한다.
    Optional<User> findByEmailAndRegistrationId(String email, String registartionId);    //id를 제외한 나머지 컬럼의 조회의 findByXXX 형태로 정의해 주어야 한다.

    // 네이티브 쿼리로 하고 싶다면
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update USERS set NICKNAME = :nickname where ID = :id", nativeQuery = true)
    int updateNickname(@Param("id") Long id, @Param("nickname") String nickname);
}
