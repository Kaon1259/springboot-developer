package com.example.springboot_developer.controller;

import com.example.springboot_developer.dto.MemberDto;
import com.example.springboot_developer.entity.Member;
import com.example.springboot_developer.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class memberController {

    private final MemberService memberService;

    public memberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/")
    public String index() {
        return "member/index";
    }

    @GetMapping("/members")
    public String getMembers(Model model) {
        List<Member> members = memberService.getMemberList();
        model.addAttribute("members", members);
        return "member/memberList";
    }

    @GetMapping("/members/new")
    public String newMember() {
        return "/member/new";
    }

    @GetMapping("/members/{id}")
    public String getMemberById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Member m = memberService.getMemberById(id).orElse(null); // orElseThrow 써도 됨
        if (m == null) {
            redirectAttributes.addFlashAttribute("message", "존재하지 않는 회원입니다. id=" + id);
            return "redirect:/members"; // 목록 등 안전한 화면으로
        }
        model.addAttribute("member", m); // ★ 뷰에서 쓸 "member" 키 넣기
        return "member/showMember";
    }

    @GetMapping("/members/{id}/delete")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
            boolean deleted = memberService.deleteMember(id);
            redirectAttributes.addFlashAttribute("message", deleted ? "삭제 하였습니다." : "삭제 실패");
            return "redirect:/members";
    }

    @GetMapping("/members/{id}/edit")
    public String editMember(@PathVariable Long id, Model model) {
        model.addAttribute("member", memberService.getMemberById(id).orElse(null));
        return "member/edit";
    }

    @PostMapping("/members/create")
    public String createMember(@Validated @ModelAttribute MemberDto dto, RedirectAttributes redirectAttributes) {
        //1. convert to Entity
        //2. Service api call
        log.info(dto.toString());
        Member saved = memberService.saveMember(dto.toEntity());

        //3. redirectAtrribute message setup
        redirectAttributes.addFlashAttribute("message", (saved == null)? "회원 가입 완료":"회원 가입 실패");
        return "redirect:/members";
    }

    @PostMapping("/members/update")
    public String updateMember(@Validated MemberDto dto, RedirectAttributes redirectAttributes) {
        log.info(dto.toString());
        boolean updated = memberService.updateMember(MemberDto.toEntity(dto));
        redirectAttributes.addFlashAttribute("message", updated ? "수정 완료":"수정 실패");
        return "redirect:/members";
    }
}
