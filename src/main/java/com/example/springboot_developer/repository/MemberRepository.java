package com.example.springboot_developer.repository;

import com.example.springboot_developer.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository<T extends Member> {
    public List<T> findAll();
    public Optional<T> findById(Long id);
    public T save(T t);
    public void deleteById(Long id);
}
