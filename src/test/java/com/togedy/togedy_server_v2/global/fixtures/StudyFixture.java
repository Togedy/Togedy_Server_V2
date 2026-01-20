package com.togedy.togedy_server_v2.global.fixtures;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.enums.StudyTag;
import com.togedy.togedy_server_v2.domain.study.enums.StudyType;

public class StudyFixture {

    private StudyFixture() {
    }

    public static Study createNormalStudy() {
        return Study.builder()
                .name("일반 스터디")
                .description("일반 스터디 생성")
                .tag(StudyTag.SCHOOL)
                .tier("티어")
                .memberLimit(10)
                .imageUrl(null)
                .type(StudyType.NORMAL)
                .build();
    }

    public static Study createChallengeStudy() {
        return Study.builder()
                .name("챌린지 스터디")
                .description("챌린지 스터디 생성")
                .tag(StudyTag.SCHOOL)
                .tier("티어")
                .memberLimit(10)
                .imageUrl(null)
                .goalTime(3 * 3600L)
                .type(StudyType.CHALLENGE)
                .build();
    }

    public static Study createNormalStudyWithImage() {
        return Study.builder()
                .name("일반 스터디")
                .description("일반 스터디 생성")
                .tag(StudyTag.SCHOOL)
                .tier("티어")
                .memberLimit(10)
                .imageUrl("https://mock-s3/test.png")
                .type(StudyType.CHALLENGE)
                .build();
    }

    public static Study createNormalStudyWithPassword() {
        return Study.builder()
                .name("일반 스터디")
                .description("일반 스터디 생성")
                .tag(StudyTag.SCHOOL)
                .tier("티어")
                .memberLimit(10)
                .imageUrl(null)
                .password("1234")
                .type(StudyType.NORMAL)
                .build();
    }

    public static Study createNormalStudyWithName(String studyName) {
        return Study.builder()
                .name(studyName)
                .description("일반 스터디 생성")
                .tag(StudyTag.SCHOOL)
                .tier("티어")
                .memberLimit(10)
                .imageUrl(null)
                .type(StudyType.NORMAL)
                .build();
    }

    public static Study createNormalStudyWithTag(StudyTag studyTag) {
        return Study.builder()
                .name("일반 스터디")
                .description("일반 스터디 생성")
                .tag(studyTag)
                .tier("티어")
                .memberLimit(10)
                .imageUrl(null)
                .type(StudyType.NORMAL)
                .build();
    }

    public static Study createChallengeStudyWithTag(StudyTag studyTag) {
        return Study.builder()
                .name("챌린지 스터디")
                .description("챌린지 스터디 생성")
                .tag(studyTag)
                .tier("티어")
                .memberLimit(10)
                .imageUrl(null)
                .goalTime(18000L)
                .type(StudyType.CHALLENGE)
                .build();
    }
}
