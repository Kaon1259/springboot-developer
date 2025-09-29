package com.example.springboot_developer.configure;

import com.example.springboot_developer.configure.jwt.JwtCustomLoginSuccessHandler;
import com.example.springboot_developer.configure.jwt.JwtLogoutHandler;
import com.example.springboot_developer.configure.jwt.TokenAuthenticationFilter;
import com.example.springboot_developer.configure.jwt.TokenProvider;
import com.example.springboot_developer.configure.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.springboot_developer.configure.oauth.OAuth2SuccessHandler;
import com.example.springboot_developer.configure.oauth.OAuth2UserCustomService;
import com.example.springboot_developer.repository.RefreshTokenRepository;
import com.example.springboot_developer.service.RefreshTokenService;
import com.example.springboot_developer.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Slf4j
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebOAuthSecurityConfigure {
    //@AutoWired
    private final OAuth2UserCustomService customService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @Bean
    JwtCustomLoginSuccessHandler  customLoginSuccessHandler(){
        return new JwtCustomLoginSuccessHandler();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("WebSecurityCustomizer");
        return (web) -> web.ignoring()
                // H2 콘솔 무시 (필터 거치지 않음)
                .requestMatchers(toH2Console())
                // 정적 리소스 무시 (필터 거치지 않음)
                .requestMatchers("/static/**")
                .requestMatchers("/css/**")
                .requestMatchers("/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("filterChain");
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                //.formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/token","/login", "/signup", "/css/**", "/js/**", "/img/**",
                                "/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/articles/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // 로그인 페이지 경로를 명시
                        .successHandler(customLoginSuccessHandler())
                      //  .defaultSuccessUrl("/articles", false)
                        .permitAll() // 모든 사용자가 로그인 페이지에 접근 가능
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(customService))
                        .successHandler(oAuth2SuccessHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        // 1. Custom Logout Handler 등록 (DB Refresh Token 삭제)
                        .addLogoutHandler(jwtLogoutHandler())
                        .invalidateHttpSession(true)
                        // 2. Logout Success Handler 등록 (HttpOnly 쿠키 삭제 명령 포함)
                        .logoutSuccessHandler((request, response, authentication) -> {
                                // 로그아웃 성공 응답
                           // response.setStatus(HttpServletResponse.SC_OK);
                        })

                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**"))
                ).build();
    }


    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        log.info("Get(): oAuth2SuccessHandler");
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        log.info("Get() : tokenAuthenticationFilter");
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        log.info("Get() : oAuth2AuthorizationRequestBasedOnCookieRepository");
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public JwtLogoutHandler jwtLogoutHandler() {
        // 실제 Repository를 주입해야 합니다. (생략)
        return new JwtLogoutHandler(userService, refreshTokenService);
    }
}
