package com.togedy.togedy_server_v2.domain.user.dto;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageStudyDto {

    private String studyName;

    private String studyImageUrl;

    private Boolean isCompleted;

    private Integer completedMemberCount;

    private Integer studyMemberCount;

    public static MyPageStudyDto from(Study study, Boolean isCompleted, int completedMemberCount) {
        return MyPageStudyDto.builder()
                .studyName(study.getName())
                .studyImageUrl(study.getImageUrl())
                .isCompleted(isCompleted)
                .completedMemberCount(completedMemberCount)
                .studyMemberCount(study.getMemberCount())
                .build();
    }

    public static MyPageStudyDto from(Study study) {
        return MyPageStudyDto.builder()
                .studyName(study.getName())
                .studyImageUrl(study.getImageUrl())
                .build();
    }
}
