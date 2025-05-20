package com.togedy.togedy_server_v2.domain.schedule.dto;

import com.togedy.togedy_server_v2.domain.schedule.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetCategoryResponse {

    private Long categoryId;
    private String categoryName;
    private String categoryColor;

    public static GetCategoryResponse from(Category category) {
        return GetCategoryResponse.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .categoryColor(category.getColor())
                .build();
    }
}
