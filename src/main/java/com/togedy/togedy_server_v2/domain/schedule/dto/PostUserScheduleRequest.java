package com.togedy.togedy_server_v2.domain.schedule.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUserScheduleRequest {

    private String userScheduleName;
    private String startDate;
    private boolean allDayStart;
    private String endDate;
    private boolean allDayEnd;
    private Long categoryId;
    private String memo;
    private boolean dDay;

}
