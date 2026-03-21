package com.togedy.togedy_server_v2.domain.planner.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PutStudyTaskRequest {
    private Long taskId;
    private Long studySubjectId;
    private String name;
    private LocalDate date;
}
