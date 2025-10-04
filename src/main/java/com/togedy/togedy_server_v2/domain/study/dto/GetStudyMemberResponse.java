package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.enums.UserStatus;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
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

    private String studyTime;

    private String lastActivatedAt;

    public static GetStudyMemberResponse of(User user, DailyStudySummary dailyStudySummary, StudyRole role) {
        return GetStudyMemberResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .studyRole(role)
                .userStatus(user.getStatus())
                .userProfileImageUrl(user.getProfileImageUrl())
                .studyTime(TimeUtil.toTimeFormat(dailyStudySummary.getStudyTime()))
                .lastActivatedAt(TimeUtil.formatTimeAgo(user.getLastActivatedAt()))
                .build();
    }

    public static GetStudyMemberResponse of(User user, StudyRole role) {
        return GetStudyMemberResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .studyRole(role)
                .userStatus(user.getStatus())
                .userProfileImageUrl(user.getProfileImageUrl())
                .lastActivatedAt(TimeUtil.formatTimeAgo(user.getLastActivatedAt()))
                .build();
    }
}
