package com.togedy.togedy_server_v2.global.security;

import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

public final class PublicEndpointPolicy {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    public static final String[] PUBLIC_ANY_METHOD_PATTERNS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/h2-console/**"
    };

    public static final String[] PUBLIC_GET_PATTERNS = {
            "/api/v2/users/nickname/validate",
            "/api/v2/users/nickname/suggestions",
            "/api/v2/calendars/announcement",
            "/api/v2/policies/**",
            "/api/v2/studies/duplicate",
            "/api/v2/studies",
            "/api/v2/studies/popular"
    };

    public static final String[] PUBLIC_POST_PATTERNS = {
            "/api/v2/auth/login",
            "/api/v2/auth/reissue",
            "/api/v2/auth/kakao",
            "/api/v2/users/sign-up"
    };

    private PublicEndpointPolicy() {
    }

    public static boolean isPublic(String method, String path) {
        if (matches(PUBLIC_ANY_METHOD_PATTERNS, path)) {
            return true;
        }

        if (HttpMethod.GET.matches(method) && matches(PUBLIC_GET_PATTERNS, path)) {
            return true;
        }

        return HttpMethod.POST.matches(method) && matches(PUBLIC_POST_PATTERNS, path);
    }

    private static boolean matches(String[] patterns, String path) {
        for (String pattern : patterns) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}
