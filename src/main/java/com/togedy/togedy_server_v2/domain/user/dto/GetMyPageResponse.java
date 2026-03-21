package com.togedy.togedy_server_v2.domain.user.dto;

import com.togedy.togedy_server_v2.domain.user.entity.User;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMyPageResponse {

    private String userName;

    private String userEmail;

    private String userProfileImageUrl;

    private String totalStudyTime;

    private Integer attendanceStreak;

    private List<MyPageStudyDto> studies;

    public static GetMyPageResponse from(User user, String totalStudyTime, List<MyPageStudyDto> studies) {
        return GetMyPageResponse.builder()
                .userName(user.getNickname())
                .userEmail(user.getEmail())
                .userProfileImageUrl(user.getProfileImageUrl())
                .totalStudyTime(totalStudyTime)
                .attendanceStreak(user.getStudyStreak())
                .studies(studies)
                .build();
    }
}
