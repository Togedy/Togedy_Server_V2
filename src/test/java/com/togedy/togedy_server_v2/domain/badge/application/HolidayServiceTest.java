package com.togedy.togedy_server_v2.domain.badge.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.togedy.togedy_server_v2.domain.badge.dao.HolidayKindRepository;
import com.togedy.togedy_server_v2.domain.badge.dao.HolidayRepository;
import com.togedy.togedy_server_v2.domain.badge.entity.Holiday;
import com.togedy.togedy_server_v2.domain.badge.entity.HolidayKind;
import com.togedy.togedy_server_v2.domain.badge.enums.DateRuleType;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HolidayServiceTest extends AbstractE2ETest {

    @Autowired
    HolidayService holidayService;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    HolidayKindRepository holidayKindRepository;

    @Test
    @DisplayName("설날·추석은 음력 규칙 날짜 앞뒤 하루에 맞닿은 주말까지 연휴 기간에 포함한다.")
    void generateLongHolidayWithAdjacentWeekend() {
        // 2028 추석: 음력 8/15 = 10/3(화) → 10/2(월)~10/4(수), 앞쪽 9/30(토)·10/1(일)이 맞닿음
        saveRuleKind("추석", DateRuleType.LUNAR, 8, 15, 1, 1);

        holidayService.generateRuleHolidays(2028);

        assertThat(holidayRepository.findAll())
                .extracting(Holiday::getStartDate, Holiday::getEndDate)
                .containsExactly(tuple(LocalDate.of(2028, 9, 30), LocalDate.of(2028, 10, 4)));
    }

    @Test
    @DisplayName("맞닿은 주말이 없으면 설날·추석은 규칙 날짜 앞뒤 하루만 연휴 기간이다.")
    void generateLongHolidayWithoutAdjacentWeekend() {
        // 2027 추석: 음력 8/15 = 9/15(수) → 9/14(화)~9/16(목)
        saveRuleKind("추석", DateRuleType.LUNAR, 8, 15, 1, 1);

        holidayService.generateRuleHolidays(2027);

        assertThat(holidayRepository.findAll())
                .extracting(Holiday::getStartDate, Holiday::getEndDate)
                .containsExactly(tuple(LocalDate.of(2027, 9, 14), LocalDate.of(2027, 9, 16)));
    }

    @Test
    @DisplayName("하루짜리 공휴일은 주말과 맞닿아도 기간을 넓히지 않는다.")
    void generateSingleDayHolidayWithoutWeekendExtension() {
        // 2027 삼일절: 3/1(월), 앞쪽 2/28(일)이 맞닿지만 넓히지 않음
        saveRuleKind("삼일절", DateRuleType.SOLAR, 3, 1, 0, 0);

        holidayService.generateRuleHolidays(2027);

        assertThat(holidayRepository.findAll())
                .extracting(Holiday::getStartDate, Holiday::getEndDate)
                .containsExactly(tuple(LocalDate.of(2027, 3, 1), LocalDate.of(2027, 3, 1)));
    }

    @Test
    @DisplayName("같은 날의 다른 종류 공휴일은 각각 생성한다.")
    void generateHolidaysOnSameDate() {
        // 2044-05-05는 어린이날과 부처님오신날(음력 4/8)이 겹친다.
        HolidayKind childrensDay = saveRuleKind("어린이날", DateRuleType.SOLAR, 5, 5, 0, 0);
        HolidayKind buddhasBirthday = saveRuleKind("부처님오신날", DateRuleType.LUNAR, 4, 8, 0, 0);

        holidayService.generateRuleHolidays(2044);

        assertThat(holidayRepository.findAll())
                .extracting(holiday -> holiday.getHolidayKind().getId(), Holiday::getStartDate)
                .containsExactlyInAnyOrder(
                        tuple(childrensDay.getId(), LocalDate.of(2044, 5, 5)),
                        tuple(buddhasBirthday.getId(), LocalDate.of(2044, 5, 5)));
    }

    @Test
    @DisplayName("이미 생성된 연휴는 다시 생성하지 않는다.")
    void skipGeneratedHolidays() {
        saveRuleKind("추석", DateRuleType.LUNAR, 8, 15, 1, 1);
        saveRuleKind("삼일절", DateRuleType.SOLAR, 3, 1, 0, 0);

        holidayService.generateRuleHolidays(2028);
        holidayService.generateRuleHolidays(2028);

        assertThat(holidayRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("날짜 규칙이 없는 종류는 생성하지 않는다.")
    void skipManualHolidayKind() {
        holidayKindRepository.save(HolidayKind.builder().name("수능").badgeEnabled(true).build());

        holidayService.generateRuleHolidays(2028);

        assertThat(holidayRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("판정이 끝난 지난 연휴는 생성하지 않는다.")
    void skipEndedHolidays() {
        saveRuleKind("삼일절", DateRuleType.SOLAR, 3, 1, 0, 0);
        saveRuleKind("크리스마스", DateRuleType.SOLAR, 12, 25, 0, 0);

        holidayService.generateRuleHolidays(TimeUtil.currentStudyDate().getYear() - 1);

        assertThat(holidayRepository.findAll()).isEmpty();
    }

    private HolidayKind saveRuleKind(String name, DateRuleType dateRuleType, int month, int day,
                                     int daysBefore, int daysAfter) {
        return holidayKindRepository.save(HolidayKind.builder()
                .name(name)
                .badgeEnabled(true)
                .dateRuleType(dateRuleType)
                .ruleMonth(month)
                .ruleDay(day)
                .daysBefore(daysBefore)
                .daysAfter(daysAfter)
                .build());
    }
}
