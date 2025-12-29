package com.togedy.togedy_server_v2.domain.global.fixtures;

import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;

public class UserStudyFixture {

    private UserStudyFixture() {
    }

    public static UserStudy createLeaderUserStudy(Long userId) {
        return UserStudy.builder()
                .userId(userId)
                .studyId(1L)
                .role(StudyRole.LEADER)
                .build();
    }

    public static UserStudy createMemberUserStudy(Long userId) {
        return UserStudy.builder()
                .userId(userId)
                .studyId(1L)
                .role(StudyRole.MEMBER)
                .build();
    }
}
