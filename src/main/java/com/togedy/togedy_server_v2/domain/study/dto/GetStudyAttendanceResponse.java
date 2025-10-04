package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStudyAttendanceResponse {

    private Long userId;

    private String userName;

    private List<String> studyTimeList;

    public static GetStudyAttendanceResponse of(User user, List<String> studyTimeList) {
        return GetStudyAttendanceResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .studyTimeList(studyTimeList)
                .build();
    }
}
