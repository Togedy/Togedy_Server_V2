package com.togedy.togedy_server_v2.domain.global.fixtures;

import com.togedy.togedy_server_v2.global.security.AuthUser;

public class AuthUserFixture {

    private AuthUserFixture() {
    }

    public static AuthUser createAuthUser(Long id) {
        return AuthUser.builder()
                .id(id)
                .build();
    }
}
