package com.togedy.togedy_server_v2.domain.schedule.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchCategoryRequest {
    private String categoryName;
    private String categoryColor;
}
