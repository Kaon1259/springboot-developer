package com.example.springboot_developer.api;


import com.example.springboot_developer.dto.UserDto;
import com.example.springboot_developer.entity.User;
import com.example.springboot_developer.service.UserService;
import com.example.springboot_developer.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserApiController {

    final private UserService userService;
    final static private String DEFAULT_REGISTARTION_ID = "email";

    @PostMapping("/user")
    public String signup(UserDto userDto, RedirectAttributes redirectAttributes) {
        userDto.setRegistrationid(DEFAULT_REGISTARTION_ID);
        log.info("signup " + userDto.toString());

        User user = userService.save(userDto);
        redirectAttributes.addFlashAttribute("message", (user != null) ? "회원 가입 완료" : "회원 가입 실패");

        return  (user != null) ? "redirect:/login" : "redirect:/signup";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        log.info("logout " + SecurityContextHolder.getContext().getAuthentication().getName());
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return "redirect:/login";
    }
}
