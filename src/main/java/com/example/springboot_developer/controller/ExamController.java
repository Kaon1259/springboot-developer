package com.example.springboot_developer.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ExamController {

    @GetMapping("/thymeleaf/example")
    public String thymeleafExample(Model model) {
        Person person = Person.builder()
                .id(1L)
                .name("홍길동")
                .age(50)
                .hobbies(List.of("독서", "운동", "명상"))
                .build();
        model.addAttribute("person", person);
        model.addAttribute("today", LocalDate.now());

        return "thymeleaf/example";
    }
}

@Getter
@Setter
@AllArgsConstructor
@Builder
class Person {
    private Long id;
    private String name;
    private int age;
    private List<String> hobbies;
}
