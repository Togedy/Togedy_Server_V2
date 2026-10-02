package com.togedy.togedy_server_v2.global.config;

import com.togedy.togedy_server_v2.domain.admin.application.AdminUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 어드민 페이지(/admin/**) 전용 세션 기반 폼 로그인 체인. JWT 체인보다 먼저 매칭되어야 하므로 @Order(1)로 등록한다.
 */
@Configuration
@RequiredArgsConstructor
public class AdminSecurityConfig {

    private final AdminUserDetailsService adminUserDetailsService;

    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/admin/**")
                .userDetailsService(adminUserDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login").permitAll()
                        .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .usernameParameter("loginId")
                        .defaultSuccessUrl("/admin", true)
                        .failureUrl("/admin/login?error")
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout")
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
