package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyMemberProfileResponse {

    private String userName;

    private Boolean studying;

    private String userProfileImageUrl;

    private String userProfileMessage;

    private String totalStudyTime;

    private Integer attendanceStreak;

    private Integer elapsedDays;

    public static GetStudyMemberProfileResponse of(
            User user,
            boolean isStudying,
            String totalStudyTime,
            int elapsedDays
    ) {
        return GetStudyMemberProfileResponse.builder()
                .userName(user.getNickname())
                .studying(isStudying)
                .userProfileImageUrl(user.getProfileImageUrl())
                .userProfileMessage(user.getProfileMessage())
                .totalStudyTime(totalStudyTime)
                .attendanceStreak(user.getStudyStreak())
                .elapsedDays(elapsedDays)
                .build();
    }
}
