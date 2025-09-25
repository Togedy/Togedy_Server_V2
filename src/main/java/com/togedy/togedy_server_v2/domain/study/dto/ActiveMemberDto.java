package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActiveMemberDto {

    private String userName;

    private String userProfileImageUrl;

    public static ActiveMemberDto from(User user) {
        return ActiveMemberDto.builder()
                .userName(user.getNickname())
                .userProfileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
