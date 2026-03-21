package com.togedy.togedy_server_v2.domain.planner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetDailyTimetableResponse {

    @JsonProperty("timeTableList")
    private List<DailyTimetableItemResponse> timeTableList;

    public static GetDailyTimetableResponse of(List<DailyTimetableItemResponse> timeTableList) {
        return GetDailyTimetableResponse.builder()
                .timeTableList(timeTableList)
                .build();
    }
}
