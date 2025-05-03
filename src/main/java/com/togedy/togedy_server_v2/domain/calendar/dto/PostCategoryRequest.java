package com.togedy.togedy_server_v2.domain.calendar.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCategoryRequest {
    private String categoryName;
    private String categoryColor;
}
