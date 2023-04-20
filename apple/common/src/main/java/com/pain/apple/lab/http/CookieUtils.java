package com.pain.apple.lab.http;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
public class CookieUtils {

    public static void addCookie(HttpServletResponse response,
                                 String domain,
                                 String path,
                                 String name,
                                 String value,
                                 int maxAge) {
        try {
            value = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("encode failed! name: {}, value: {}", name, value, e);
        }

        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static void removeCookie(HttpServletResponse response,
                                    String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                // decode
                return cookie.getValue();
            }
        }

        return null;
    }
}
