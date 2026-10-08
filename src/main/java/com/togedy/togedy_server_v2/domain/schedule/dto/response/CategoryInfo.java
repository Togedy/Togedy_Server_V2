package com.togedy.togedy_server_v2.domain.schedule.dto.response;

import com.togedy.togedy_server_v2.domain.schedule.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryInfo {

    private Long categoryId;
    private String categoryName;
    private String categoryColor;

    public static CategoryInfo from(Category category) {
        return CategoryInfo.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .categoryColor(category.getColor())
                .build();
    }

    public static CategoryInfo temp() {
        return CategoryInfo.builder()
                .build();
    }
}
