package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.enums.UserStatus;
import com.togedy.togedy_server_v2.global.util.DateTimeUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyMemberResponse {

    private Long userId;

    private String userName;

    private StudyRole studyRole;

    private UserStatus userStatus;

    private String userProfileImageUrl;

    private String lastActivatedAt;

    public static GetStudyMemberResponse of(User user, StudyRole role) {
        return GetStudyMemberResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .studyRole(role)
                .userStatus(user.getStatus())
                .userProfileImageUrl(user.getProfileImageUrl())
                .lastActivatedAt(DateTimeUtils.formatTimeAgo(user.getLastActivatedAt()))
                .build();
    }
}
