package com.togedy.togedy_server_v2.domain.badge.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.togedy.togedy_server_v2.domain.badge.dao.BadgeRepository;
import com.togedy.togedy_server_v2.domain.badge.entity.Badge;
import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import com.togedy.togedy_server_v2.domain.planner.application.TimerService;
import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStopRequest;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

class TimerStoppedEventHandlerTest extends AbstractE2ETest {

    @Autowired
    TimerService timerService;

    @Autowired
    BadgeRepository badgeRepository;

    @Autowired
    StudySubjectRepository studySubjectRepository;

    @Autowired
    StudyTimeRepository studyTimeRepository;

    @Autowired
    DailyStudySummaryRepository dailyStudySummaryRepository;

    @Test
    @DisplayName("타이머 종료가 커밋되면 한우물왕과 월간 파이터 뱃지를 지급한다.")
    void grantBadgesAfterTimerStop() {
        User user = fixtureSupport.persistUser(UserFixture.createUser());
        StudySubject korean = studySubjectRepository.save(StudySubject.builder()
                .userId(user.getId())
                .name("국어")
                .color("#000000")
                .orderIndex(1L)
                .build());
        LocalDateTime now = TimeUtil.nowInStudyZone();
        studyTimeRepository.save(StudyTime.builder()
                .userId(user.getId())
                .studySubjectId(korean.getId())
                .startTime(now.minusDays(3))
                .endTime(now.minusDays(3).plusHours(30))
                .build());
        dailyStudySummaryRepository.save(DailyStudySummary.builder()
                .userId(user.getId())
                .date(TimeUtil.currentStudyDate())
                .studyTime(50 * 3600L)
                .build());
        StudyTime running = studyTimeRepository.save(StudyTime.builder()
                .userId(user.getId())
                .studySubjectId(korean.getId())
                .startTime(now.minusMinutes(1))
                .build());
        PostTimerStopRequest request = new PostTimerStopRequest();
        ReflectionTestUtils.setField(request, "timerId", running.getId());

        timerService.stopTimer(request, user.getId());

        assertThat(badgeRepository.findAll())
                .extracting(Badge::getType)
                .containsExactlyInAnyOrder(BadgeType.ONE_SUBJECT_30, BadgeType.MONTHLY_50);
    }
}
