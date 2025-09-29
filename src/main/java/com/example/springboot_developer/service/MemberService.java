package com.example.springboot_developer.service;

import com.example.springboot_developer.entity.Member;
import com.example.springboot_developer.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MemberService {

    final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getMemberList() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Member saveMember(Member member) { return this.memberRepository.save(member); }

    public boolean deleteMember(Long id) {
        memberRepository.deleteById(id);
        Optional<Member> member = memberRepository.findById(id);
        return member.isEmpty();
    }

    public boolean updateMember(Member member) {
        Optional<Member> UpdatedMember = this.memberRepository.findById(member.getId());

        if (UpdatedMember.isPresent()) {
            UpdatedMember.get().setName(member.getName());
            UpdatedMember.get().setAge(member.getAge());

            Member savedMember = saveMember(UpdatedMember.get());

            return savedMember != null;
        }

        return false;
    }
}
