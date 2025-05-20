package com.togedy.togedy_server_v2.domain.schedule.dto;

import com.togedy.togedy_server_v2.domain.schedule.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryDto {

    private Long categoryId;
    private String categoryName;
    private String categoryColor;

    public static CategoryDto from(Category category) {
        return CategoryDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .categoryColor(category.getColor())
                .build();
    }

    public static CategoryDto temp() {
        return CategoryDto.builder()
                .build();
    }
}
