package com.example.springboot_developer.repository;

import com.example.springboot_developer.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface SpringJpaDataRepository extends JpaRepository<Member, Long>, MemberRepository<Member> {
    @Override
    public List<Member> findAll();

    @Override
    public Optional<Member> findById(Long id);

    @Override
    public Member save(Member entity);

    @Override
    public void deleteById(Long id);
}
