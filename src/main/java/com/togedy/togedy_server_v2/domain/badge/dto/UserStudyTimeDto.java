package com.togedy.togedy_server_v2.domain.badge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserStudyTimeDto {
    private Long userId;
    private Long studyTime;
}
