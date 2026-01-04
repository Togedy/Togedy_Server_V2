package com.togedy.togedy_server_v2.domain.planner.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchStudyCategoryRequest {
    private String categoryName;
    private String categoryColor;
}
