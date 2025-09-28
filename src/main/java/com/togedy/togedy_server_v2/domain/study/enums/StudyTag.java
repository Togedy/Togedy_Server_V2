package com.togedy.togedy_server_v2.domain.study.enums;

import com.togedy.togedy_server_v2.domain.study.exception.InvalidStudyTagException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum StudyTag {
    SCHOOL("내신/학교생활"),
    UNIVERSITY("대학입시/편입"),
    JOB("취업/자격증"),
    FREE("자유스터디");

    private final String description;

    public static StudyTag fromDescription(String description) {
        return Arrays.stream(values())
                .filter(studyTag -> studyTag.getDescription().equals(description))
                .findFirst()
                .orElseThrow(InvalidStudyTagException::new);
    }
}
