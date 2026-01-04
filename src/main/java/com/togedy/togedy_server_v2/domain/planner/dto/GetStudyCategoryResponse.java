package com.togedy.togedy_server_v2.domain.planner.dto;

import com.togedy.togedy_server_v2.domain.planner.entity.StudyCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyCategoryResponse {

    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private Long orderIndex;

    public static GetStudyCategoryResponse from(StudyCategory studyCategory) {
        return GetStudyCategoryResponse.builder()
                .categoryId(studyCategory.getId())
                .categoryName(studyCategory.getName())
                .categoryColor(studyCategory.getColor())
                .orderIndex(studyCategory.getOrderIndex())
                .build();
    }
}
