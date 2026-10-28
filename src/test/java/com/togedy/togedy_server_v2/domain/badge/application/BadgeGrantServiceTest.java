package com.togedy.togedy_server_v2.domain.badge.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.togedy.togedy_server_v2.domain.badge.dao.BadgeRepository;
import com.togedy.togedy_server_v2.domain.badge.entity.Badge;
import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BadgeGrantServiceTest extends AbstractE2ETest {

    private static final Long USER_ID = 1L;
    private static final LocalDate STUDY_DATE = LocalDate.of(2026, 10, 27);

    @Autowired
    BadgeGrantService badgeGrantService;

    @Autowired
    BadgeRepository badgeRepository;

    @Autowired
    StudySubjectRepository studySubjectRepository;

    @Autowired
    StudyTimeRepository studyTimeRepository;

    @Test
    @DisplayName("판정 학습일에 기준 시간에 도달한 과목에 과목명을 담아 한우물왕 뱃지를 지급한다.")
    void grantOneSubjectBadge() {
        StudySubject korean = saveSubject("국어");
        saveStudyTime(korean, LocalDateTime.of(2026, 10, 20, 6, 0), 25);
        saveStudyTime(korean, LocalDateTime.of(2026, 10, 27, 6, 0), 5);

        badgeGrantService.grantOneSubjectBadges(STUDY_DATE);

        assertThat(badgeRepository.findAll())
                .singleElement()
                .satisfies(badge -> {
                    assertThat(badge.getType()).isEqualTo(BadgeType.ONE_SUBJECT_30);
                    assertThat(badge.getDetail()).isEqualTo("국어");
                    assertThat(badge.getPeriodKey()).isEqualTo("ONCE");
                });
    }

    @Test
    @DisplayName("삭제된 과목은 제외하고 다음으로 도달한 과목에 지급한다.")
    void skipDeletedSubject() {
        StudySubject korean = saveSubject("국어");
        StudySubject math = saveSubject("수학");
        saveStudyTime(korean, LocalDateTime.of(2026, 10, 20, 6, 0), 30);
        saveStudyTime(math, LocalDateTime.of(2026, 10, 22, 6, 0), 25);
        saveStudyTime(math, LocalDateTime.of(2026, 10, 27, 6, 0), 5);
        korean.delete();
        studySubjectRepository.save(korean);

        badgeGrantService.grantOneSubjectBadges(STUDY_DATE);

        assertThat(badgeRepository.findAll())
                .extracting(Badge::getDetail)
                .containsExactly("수학");
    }

    @Test
    @DisplayName("이미 받은 단계는 다시 지급하지 않는다.")
    void skipGrantedType() {
        StudySubject korean = saveSubject("국어");
        saveStudyTime(korean, LocalDateTime.of(2026, 10, 20, 6, 0), 25);
        saveStudyTime(korean, LocalDateTime.of(2026, 10, 27, 6, 0), 6);

        badgeGrantService.grantOneSubjectBadges(STUDY_DATE);
        badgeGrantService.grantOneSubjectBadges(STUDY_DATE);

        assertThat(badgeRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("판정 학습일에 공부하지 않은 유저는 판정하지 않는다.")
    void skipUserNotStudiedOnStudyDate() {
        StudySubject korean = saveSubject("국어");
        saveStudyTime(korean, LocalDateTime.of(2026, 10, 20, 6, 0), 30);

        badgeGrantService.grantOneSubjectBadges(STUDY_DATE);

        assertThat(badgeRepository.findAll()).isEmpty();
    }

    private StudySubject saveSubject(String name) {
        return studySubjectRepository.save(StudySubject.builder()
                .userId(USER_ID)
                .name(name)
                .color("#000000")
                .orderIndex(1L)
                .build());
    }

    private void saveStudyTime(StudySubject subject, LocalDateTime startTime, int hours) {
        studyTimeRepository.save(StudyTime.builder()
                .userId(USER_ID)
                .studySubjectId(subject.getId())
                .startTime(startTime)
                .endTime(startTime.plusHours(hours))
                .build());
    }
}
