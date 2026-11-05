package com.togedy.togedy_server_v2.domain.badge.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.togedy.togedy_server_v2.domain.badge.dao.BadgeRepository;
import com.togedy.togedy_server_v2.domain.badge.entity.Badge;
import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import com.togedy.togedy_server_v2.domain.planner.application.TimerService;
import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartRequest;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

class TimerStartedEventHandlerTest extends AbstractE2ETest {

    @Autowired
    TimerService timerService;

    @Autowired
    BadgeRepository badgeRepository;

    @Autowired
    StudySubjectRepository studySubjectRepository;

    @Autowired
    DailyStudySummaryRepository dailyStudySummaryRepository;

    @Test
    @DisplayName("타이머 시작이 커밋되면 복귀 뱃지를 지급한다.")
    void grantComebackBadgeAfterTimerStart() {
        User user = fixtureSupport.persistUser(UserFixture.createUser());
        StudySubject korean = studySubjectRepository.save(StudySubject.builder()
                .userId(user.getId())
                .name("국어")
                .color("#000000")
                .orderIndex(1L)
                .build());
        dailyStudySummaryRepository.save(DailyStudySummary.builder()
                .userId(user.getId())
                .date(TimeUtil.currentStudyDate().minusDays(30))
                .studyTime(3600L)
                .build());
        PostTimerStartRequest request = new PostTimerStartRequest();
        ReflectionTestUtils.setField(request, "subjectId", korean.getId());

        timerService.startTimer(request, user.getId());

        assertThat(badgeRepository.findAll())
                .extracting(Badge::getType)
                .containsExactly(BadgeType.COMEBACK);
    }
}
