package com.togedy.togedy_server_v2.domain.planner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTimerTotalResponse {

    private Long studyTime;

    public static GetTimerTotalResponse of(Long studyTime) {
        return GetTimerTotalResponse.builder()
                .studyTime(studyTime)
                .build();
    }
}
