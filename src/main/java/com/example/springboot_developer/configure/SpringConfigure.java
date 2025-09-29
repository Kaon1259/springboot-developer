package com.example.springboot_developer.configure;

import com.example.springboot_developer.repository.MemberRepository;
import com.example.springboot_developer.service.MemberService;
import com.example.springboot_developer.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SpringConfigure {

//    final public MemberRepository memberRepository;
    //final private UserDetailService userService;

    //@RequiredArgConstructor 노테이션을 추가하면 final 에 대해서는 @Autowired로 자동 생성되어 주입된다.
//    @Autowired
//    SpringConfigure(MemberRepository memberRepository, UserDetailService userService){
//        this.memberRepository = memberRepository;
//        this.userService = userService;
//    }

//    @Bean
//    public MemberService memberService() {
//        return new MemberService(memberRepository);
//    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring()
//                // H2 콘솔 무시 (필터 거치지 않음)
//                .requestMatchers(toH2Console())
//                // 정적 리소스 무시 (필터 거치지 않음)
//                .requestMatchers("/static/**");
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(csrf -> csrf.disable()) // 쿠키 세션 쓰면 유지 고려
//                .authorizeHttpRequests(auth -> auth
//                        // 정적 리소스 열기 (필요 시)
//                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
//
//                        // 로그인/회원가입/유저 공개 엔드포인트
//                        .requestMatchers(HttpMethod.GET, "/login", "/signup", "/user/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/signup").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
//
//                        // 나머지는 인증 필요
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")            // GET /login 은 위에서 permitAll
//                        .defaultSuccessUrl("/articles", false)
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")           // 기본값 /logout (POST). GET 사용 시 추가 설정 필요
//                        .logoutSuccessUrl("/login")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                )
//                .build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userService);
//        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
//        return new ProviderManager(authProvider);
//    }
//
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
