package com.togedy.togedy_server_v2.domain.schedule.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchUserScheduleRequest {

    private String userScheduleName;
    private String startDate;
    private Boolean allDayStart;
    private String endDate;
    private Boolean allDayEnd;
    private Long categoryId;
    private String memo;
    private Boolean dDay;

}
