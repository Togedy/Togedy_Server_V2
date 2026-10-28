package com.togedy.togedy_server_v2.domain.badge.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.togedy.togedy_server_v2.domain.badge.dao.BadgeRepository;
import com.togedy.togedy_server_v2.domain.badge.dao.HolidayRepository;
import com.togedy.togedy_server_v2.domain.badge.entity.Badge;
import com.togedy.togedy_server_v2.domain.badge.entity.Holiday;
import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
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
    private static final LocalDate SATURDAY = LocalDate.of(2026, 10, 24);
    private static final LocalDate SUNDAY = LocalDate.of(2026, 10, 25);
    private static final LocalDate HOLIDAY_START = LocalDate.of(2026, 9, 24);
    private static final LocalDate HOLIDAY_END = LocalDate.of(2026, 9, 26);

    @Autowired
    BadgeGrantService badgeGrantService;

    @Autowired
    BadgeRepository badgeRepository;

    @Autowired
    StudySubjectRepository studySubjectRepository;

    @Autowired
    StudyTimeRepository studyTimeRepository;

    @Autowired
    DailyStudySummaryRepository dailyStudySummaryRepository;

    @Autowired
    HolidayRepository holidayRepository;

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

    @Test
    @DisplayName("토·일 합계가 10시간 이상이면 해당 주 토요일을 기간 키로 주말 파이터 10시간 뱃지를 지급한다.")
    void grantWeekend10Badge() {
        saveDailyStudySummary(USER_ID, SATURDAY, 4);
        saveDailyStudySummary(USER_ID, SUNDAY, 6);

        badgeGrantService.grantWeekendBadges(SUNDAY);

        assertThat(badgeRepository.findAll())
                .singleElement()
                .satisfies(badge -> {
                    assertThat(badge.getType()).isEqualTo(BadgeType.WEEKEND_10);
                    assertThat(badge.getPeriodKey()).isEqualTo(SATURDAY.toString());
                });
    }

    @Test
    @DisplayName("토·일 합계가 20시간 이상이면 20시간 뱃지만 지급한다.")
    void grantOnlyWeekend20Badge() {
        saveDailyStudySummary(USER_ID, SATURDAY, 10);
        saveDailyStudySummary(USER_ID, SUNDAY, 10);

        badgeGrantService.grantWeekendBadges(SUNDAY);

        assertThat(badgeRepository.findAll())
                .extracting(Badge::getType)
                .containsExactly(BadgeType.WEEKEND_20);
    }

    @Test
    @DisplayName("토·일 합계가 20시간 이상이면 10시간 뱃지는 지급하지 않는다.")
    void skipWeekend10BadgeWhenReached20() {
        saveDailyStudySummary(USER_ID, SATURDAY, 12);
        saveDailyStudySummary(USER_ID, SUNDAY, 13);

        badgeGrantService.grantWeekendBadges(SUNDAY);

        assertThat(badgeRepository.findAll())
                .extracting(Badge::getType)
                .doesNotContain(BadgeType.WEEKEND_10);
    }

    @Test
    @DisplayName("주말 외의 공부 시간과 10시간 미만인 유저는 지급하지 않는다.")
    void skipBelowWeekendThreshold() {
        saveDailyStudySummary(USER_ID, SATURDAY.minusDays(1), 10);
        saveDailyStudySummary(USER_ID, SATURDAY, 9);
        saveDailyStudySummary(2L, SUNDAY, 9);

        badgeGrantService.grantWeekendBadges(SUNDAY);

        assertThat(badgeRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("판정 학습일이 일요일이 아니면 주말 파이터를 판정하지 않는다.")
    void skipWeekendOnNonSunday() {
        saveDailyStudySummary(USER_ID, SATURDAY, 10);

        badgeGrantService.grantWeekendBadges(SATURDAY);

        assertThat(badgeRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("주마다 다시 지급하고, 같은 주는 다시 지급하지 않는다.")
    void grantWeekendBadgeEveryWeek() {
        saveDailyStudySummary(USER_ID, SATURDAY.minusWeeks(1), 10);
        saveDailyStudySummary(USER_ID, SATURDAY, 10);

        badgeGrantService.grantWeekendBadges(SUNDAY.minusWeeks(1));
        badgeGrantService.grantWeekendBadges(SUNDAY);
        badgeGrantService.grantWeekendBadges(SUNDAY);

        assertThat(badgeRepository.findAll())
                .extracting(Badge::getPeriodKey)
                .containsExactlyInAnyOrder(SATURDAY.minusWeeks(1).toString(), SATURDAY.toString());
    }

    @Test
    @DisplayName("연휴의 모든 날 4시간 이상 공부하면 연휴명과 연휴 시작일을 담아 연휴 파이터 뱃지를 지급한다.")
    void grantHolidayBadge() {
        Holiday holiday = saveHoliday("추석 연휴", HOLIDAY_START, HOLIDAY_END);
        saveDailyStudySummary(USER_ID, HOLIDAY_START, 4);
        saveDailyStudySummary(USER_ID, HOLIDAY_START.plusDays(1), 5);
        saveDailyStudySummary(USER_ID, HOLIDAY_END, 4);

        badgeGrantService.grantHolidayBadges(HOLIDAY_END);

        assertThat(badgeRepository.findAll())
                .singleElement()
                .satisfies(badge -> {
                    assertThat(badge.getType()).isEqualTo(BadgeType.HOLIDAY);
                    assertThat(badge.getDetail()).isEqualTo(holiday.getName());
                    assertThat(badge.getPeriodKey()).isEqualTo(HOLIDAY_START.toString());
                });
    }

    @Test
    @DisplayName("연휴 중 하루라도 4시간 미만이거나 공부하지 않은 날이 있으면 지급하지 않는다.")
    void skipHolidayBadgeWhenAnyDayMissed() {
        saveHoliday("추석 연휴", HOLIDAY_START, HOLIDAY_END);
        saveDailyStudySummary(USER_ID, HOLIDAY_START, 4);
        saveDailyStudySummary(USER_ID, HOLIDAY_START.plusDays(1), 3);
        saveDailyStudySummary(USER_ID, HOLIDAY_END, 4);
        saveDailyStudySummary(2L, HOLIDAY_START, 4);
        saveDailyStudySummary(2L, HOLIDAY_END, 4);

        badgeGrantService.grantHolidayBadges(HOLIDAY_END);

        assertThat(badgeRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("판정 학습일이 연휴 종료일이 아니면 연휴 파이터를 판정하지 않는다.")
    void skipHolidayBadgeBeforeEndDate() {
        saveHoliday("추석 연휴", HOLIDAY_START, HOLIDAY_END);
        saveDailyStudySummary(USER_ID, HOLIDAY_START, 4);
        saveDailyStudySummary(USER_ID, HOLIDAY_START.plusDays(1), 4);

        badgeGrantService.grantHolidayBadges(HOLIDAY_START.plusDays(1));

        assertThat(badgeRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("같은 연휴는 다시 지급하지 않는다.")
    void skipGrantedHolidayBadge() {
        saveHoliday("추석 연휴", HOLIDAY_START, HOLIDAY_END);
        saveDailyStudySummary(USER_ID, HOLIDAY_START, 4);
        saveDailyStudySummary(USER_ID, HOLIDAY_START.plusDays(1), 4);
        saveDailyStudySummary(USER_ID, HOLIDAY_END, 4);

        badgeGrantService.grantHolidayBadges(HOLIDAY_END);
        badgeGrantService.grantHolidayBadges(HOLIDAY_END);

        assertThat(badgeRepository.findAll()).hasSize(1);
    }

    private Holiday saveHoliday(String name, LocalDate startDate, LocalDate endDate) {
        return holidayRepository.save(Holiday.builder()
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .build());
    }

    private void saveDailyStudySummary(Long userId, LocalDate date, int hours) {
        dailyStudySummaryRepository.save(DailyStudySummary.builder()
                .userId(userId)
                .date(date)
                .studyTime(hours * 3600L)
                .build());
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
