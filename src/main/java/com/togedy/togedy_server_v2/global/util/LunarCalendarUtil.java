package com.togedy.togedy_server_v2.global.util;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 한국 음력(단기력) 날짜를 양력으로 변환한다.
 * <p>
 * 중국 음력은 베이징(UTC+8) 기준이라 한국 음력과 하루씩 어긋나는 해가 있으므로(예: 2028 설날), 서울(UTC+9) 기준인 ICU dangi 달력을 사용한다.
 * </p>
 */
public class LunarCalendarUtil {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final TimeZone KOREA_TIME_ZONE = TimeZone.getTimeZone("Asia/Seoul");
    private static final ULocale DANGI_LOCALE = new ULocale("ko_KR@calendar=dangi");

    private LunarCalendarUtil() {
    }

    /**
     * 양력 연도에 속한 음력 월·일(평달)을 양력 날짜로 변환한다.
     * <p>
     * 음력 1월 1일은 항상 양력 1~2월이므로, 양력 연도 중간(7월 1일)이 속한 음력 연도를 기준 음력 연도로 삼는다.
     * </p>
     *
     * @param solarYear  양력 연도
     * @param lunarMonth 음력 월
     * @param lunarDay   음력 일
     * @return 양력 날짜
     */
    public static LocalDate toSolarDate(int solarYear, int lunarMonth, int lunarDay) {
        // ICU Calendar는 스레드 안전하지 않아 호출마다 생성한다.
        Calendar lunar = Calendar.getInstance(KOREA_TIME_ZONE, DANGI_LOCALE);
        lunar.setTimeInMillis(toEpochMilli(LocalDate.of(solarYear, 7, 1)));
        int lunarExtendedYear = lunar.get(Calendar.EXTENDED_YEAR);

        lunar.clear();
        lunar.set(Calendar.EXTENDED_YEAR, lunarExtendedYear);
        lunar.set(Calendar.MONTH, lunarMonth - 1);
        lunar.set(Calendar.IS_LEAP_MONTH, 0);
        lunar.set(Calendar.DAY_OF_MONTH, lunarDay);

        return Instant.ofEpochMilli(lunar.getTimeInMillis()).atZone(KOREA_ZONE).toLocalDate();
    }

    private static long toEpochMilli(LocalDate date) {
        return date.atStartOfDay(KOREA_ZONE).toInstant().toEpochMilli();
    }
}
