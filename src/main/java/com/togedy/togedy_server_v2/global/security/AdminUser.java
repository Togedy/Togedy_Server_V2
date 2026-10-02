package com.togedy.togedy_server_v2.global.security;

import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 어드민 페이지 세션 로그인용 principal. 인증 후 세션에 저장되므로 비밀번호는 인증 직후 제거된다.
 */
@Getter
@Builder
public class AdminUser implements UserDetails, CredentialsContainer {

    private final Long id;
    private final String loginId;
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
