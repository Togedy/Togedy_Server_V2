package com.togedy.togedy_server_v2.domain.global.fixtures;

import com.togedy.togedy_server_v2.domain.user.entity.User;

public class UserFixture {

    private UserFixture() {
    }

    public static User createUser() {
        return User.create("유저", "test@test.com");
    }
}
