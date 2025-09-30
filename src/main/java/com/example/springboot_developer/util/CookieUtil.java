package com.example.springboot_developer.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SerializationUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Slf4j
public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {

        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    //name의 쿠키를 삭제한다. --MaxAge = 0 이면 삭제된다.
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        log.info("delete cookie : " + name);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    cookie.setValue(null);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        // 1. 요청에서 모든 쿠키를 가져옵니다.
        Cookie[] cookies = request.getCookies();

        // 2. 쿠키 배열이 null이 아닌 경우에만 처리합니다.
        if (cookies != null) {
            // 3. 배열을 Stream으로 변환하고, 이름이 일치하는 쿠키를 필터링합니다.
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(name))
                    .findFirst(); // 4. 일치하는 첫 번째 쿠키를 Optional로 반환합니다.
        }

        // 5. 쿠키가 아예 없거나 일치하는 쿠키가 없으면 Optional.empty()를 반환합니다.
        return Optional.empty();
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        return getCookie(request, name)
                .map(c -> URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8))
                .orElse(null);
    }

    //객체를 직렬화해 쿠키값으로 변환 -- 쿠키 저장 값
    public static String serialize(Object object) {
        //1. object를 시리얼라이즈 하여 바이트 코드로 만들고
        //2. 바이트 코드를 베이스64 스트링으로 변환하여 반환한다.
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    //쿠키를 역직렬화해서 객체 값으로 변환
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        byte[] bytes = Base64.getUrlDecoder().decode(cookie.getValue());
        Object deserialized = SerializationUtils.deserialize(bytes);
        return cls.cast(deserialized);
    }
}
