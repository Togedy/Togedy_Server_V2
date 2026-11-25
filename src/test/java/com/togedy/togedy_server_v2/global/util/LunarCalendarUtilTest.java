package com.togedy.togedy_server_v2.global.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LunarCalendarUtilTest {

    @ParameterizedTest(name = "{0}년 음력 {1}/{2} → {3}")
    @CsvSource({
            "2026, 1, 1, 2026-02-17",
            "2026, 4, 8, 2026-05-24",
            "2026, 8, 15, 2026-09-25",
            "2028, 8, 15, 2028-10-03",
            "2044, 4, 8, 2044-05-05"
    })
    @DisplayName("한국 음력 월·일을 양력 날짜로 변환한다.")
    void toSolarDate(int year, int lunarMonth, int lunarDay, LocalDate expected) {
        assertThat(LunarCalendarUtil.toSolarDate(year, lunarMonth, lunarDay)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0}년 음력 {1}/{2} → {3}")
    @CsvSource({
            "2028, 1, 1, 2028-01-27",
            "2030, 1, 1, 2030-02-03",
            "2040, 8, 15, 2040-09-21"
    })
    @DisplayName("중국 음력(UTC+8)과 날짜가 다른 해도 한국 음력(UTC+9) 기준으로 변환한다.")
    void toSolarDateByKoreanStandard(int year, int lunarMonth, int lunarDay, LocalDate expected) {
        assertThat(LunarCalendarUtil.toSolarDate(year, lunarMonth, lunarDay)).isEqualTo(expected);
    }
}
