package com.togedy.togedy_server_v2.domain.global.fixtures;

import com.togedy.togedy_server_v2.domain.study.entity.Study;

import java.time.LocalTime;

public class StudyFixture {

    private StudyFixture() {
    }

    public static Study createNormalStudy() {
        return Study.builder()
                .name("일반 스터디")
                .description("일반 스터디 생성")
                .tag("내신/학교생활")
                .tier("티어")
                .memberLimit(10)
                .imageUrl(null)
                .type("NORMAL")
                .build();
    }

    public static Study createChallengeStudy() {
        return Study.builder()
                .name("챌린지 스터디")
                .description("챌린지 스터디 생성")
                .tag("내신/학교생활")
                .tier("티어")
                .memberLimit(10)
                .imageUrl(null)
                .goalTime(LocalTime.of(5, 0, 0))
                .type("CHALLENGE")
                .build();
    }

    public static Study createNormalStudyWithImage() {
        return Study.builder()
                .name("일반 스터디")
                .description("일반 스터디 생성")
                .tag("내신/학교생활")
                .tier("티어")
                .memberLimit(10)
                .imageUrl("https://mock-s3/test.png")
                .type("NORMAL")
                .build();
    }

    public static Study createNormalStudyWithPassword() {
        return Study.builder()
                .name("일반 스터디")
                .description("일반 스터디 생성")
                .tag("내신/학교생활")
                .tier("티어")
                .memberLimit(10)
                .imageUrl(null)
                .password("1234")
                .type("NORMAL")
                .build();
    }
}
