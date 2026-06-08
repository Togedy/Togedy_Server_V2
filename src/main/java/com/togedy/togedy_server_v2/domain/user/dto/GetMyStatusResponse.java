package com.togedy.togedy_server_v2.domain.user.dto;

import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMyStatusResponse {

    private Boolean profileCompleted;

    public static GetMyStatusResponse from(User user) {
        return GetMyStatusResponse.builder()
                .profileCompleted(user.isProfileCompleted())
                .build();
    }
}
