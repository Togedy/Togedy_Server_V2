package com.togedy.togedy_server_v2.domain.planner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PutStudyTaskRequest {
    private Long taskId;

    private Long subjectId;

    @NotBlank
    private String name;

    private LocalDate date;
}
