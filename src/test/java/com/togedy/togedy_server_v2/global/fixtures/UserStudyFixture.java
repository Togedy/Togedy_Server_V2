package com.togedy.togedy_server_v2.global.fixtures;

import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;

public class UserStudyFixture {

    private UserStudyFixture() {
    }

    public static UserStudy createLeaderUserStudy(Long userId, Long studyId) {
        return UserStudy.builder()
                .userId(userId)
                .studyId(studyId)
                .role(StudyRole.LEADER)
                .build();
    }

    public static UserStudy createMemberUserStudy(Long userId, Long studyId) {
        return UserStudy.builder()
                .userId(userId)
                .studyId(studyId)
                .role(StudyRole.MEMBER)
                .build();
    }
}
