package com.togedy.togedy_server_v2.global.security.jwt;

import com.togedy.togedy_server_v2.global.security.PublicEndpointPolicy;
import com.togedy.togedy_server_v2.global.security.jwt.exception.JwtException;
import com.togedy.togedy_server_v2.global.security.jwt.exception.JwtInvalidException;
import com.togedy.togedy_server_v2.global.security.jwt.exception.JwtInvalidFormatException;
import com.togedy.togedy_server_v2.global.security.jwt.exception.JwtNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        try {
            if (PublicEndpointPolicy.isPublic(method, requestURI)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (bearerToken == null) {
                throw new JwtNotFoundException();
            }

            if (!bearerToken.startsWith(JwtTokenProvider.BEARER)) {
                throw new JwtInvalidFormatException();
            }

            String token = jwtTokenProvider.removeBearerPrefix(bearerToken);
            if (jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new JwtInvalidException();
            }

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            throw e;
        }
    }
}
