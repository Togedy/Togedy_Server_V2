package com.togedy.togedy_server_v2.domain.badge.api.e2e;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.togedy.togedy_server_v2.domain.badge.dao.BadgeRepository;
import com.togedy.togedy_server_v2.domain.badge.dao.HolidayKindRepository;
import com.togedy.togedy_server_v2.domain.badge.dao.HolidayRepository;
import com.togedy.togedy_server_v2.domain.badge.entity.Badge;
import com.togedy.togedy_server_v2.domain.badge.entity.Holiday;
import com.togedy.togedy_server_v2.domain.badge.entity.HolidayKind;
import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Tag("E2E 테스트")
public class BadgeE2ETest extends AbstractE2ETest {

    @Autowired
    BadgeRepository badgeRepository;

    @Autowired
    HolidayKindRepository holidayKindRepository;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final String CHUSEOK_IMAGE_URL = "https://s3/badge/chuseok.png";

    @Test
    @DisplayName("보유 뱃지를 연도별, 표시 이름별로 묶어 최신순으로 조회한다.")
    public void readMyBadges() throws Exception {
        User user = fixtureSupport.persistUser(UserFixture.createUser());
        String accessToken = testJwtFactory.createAccessToken(user.getId());

        Badge oldWeekend = saveBadge(user, BadgeType.WEEKEND_10, null,
                LocalDate.of(2026, 10, 3), LocalDateTime.of(2026, 10, 5, 5, 10));
        saveHoliday("추석", CHUSEOK_IMAGE_URL, LocalDate.of(2026, 9, 24), LocalDate.of(2026, 9, 28));
        Badge holiday = saveBadge(user, BadgeType.HOLIDAY, "추석",
                LocalDate.of(2026, 9, 24), LocalDateTime.of(2026, 10, 10, 5, 10));
        Badge latestWeekend = saveBadge(user, BadgeType.WEEKEND_10, null,
                LocalDate.of(2026, 10, 24), LocalDateTime.of(2026, 10, 26, 5, 10));
        // 05:00 이전 지급은 전날 학습일이므로 2025년으로 묶인다.
        Badge comeback = saveBadge(user, BadgeType.COMEBACK, null,
                LocalDate.of(2025, 12, 31), LocalDateTime.of(2026, 1, 1, 4, 0));
        jdbcTemplate.update("UPDATE users SET representative_badge_id = ? WHERE user_id = ?",
                holiday.getId(), user.getId());

        mockMvc.perform(get("/api/v2/users/me/badges")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.representativeBadge.badgeId").value(holiday.getId()))
                .andExpect(jsonPath("$.response.representativeBadge.badgeName").value("추석 연휴 파이터"))
                .andExpect(jsonPath("$.response.representativeBadge.badgeImageUrl").value(CHUSEOK_IMAGE_URL))
                .andExpect(jsonPath("$.response.representativeBadge.acquiredDate").value("2026-10-10"))
                .andExpect(jsonPath("$.response.yearlyBadges.length()").value(2))
                .andExpect(jsonPath("$.response.yearlyBadges[0].year").value(2026))
                .andExpect(jsonPath("$.response.yearlyBadges[0].badges.length()").value(2))
                .andExpect(jsonPath("$.response.yearlyBadges[0].badges[0].badgeId").value(latestWeekend.getId()))
                .andExpect(jsonPath("$.response.yearlyBadges[0].badges[0].badgeName").value("10시간 주말 파이터"))
                .andExpect(jsonPath("$.response.yearlyBadges[0].badges[0].badgeCount").value(2))
                .andExpect(jsonPath("$.response.yearlyBadges[0].badges[1].badgeId").value(holiday.getId()))
                .andExpect(jsonPath("$.response.yearlyBadges[0].badges[1].badgeImageUrl").value(CHUSEOK_IMAGE_URL))
                .andExpect(jsonPath("$.response.yearlyBadges[0].badges[1].badgeCount").value(1))
                .andExpect(jsonPath("$.response.yearlyBadges[1].year").value(2025))
                .andExpect(jsonPath("$.response.yearlyBadges[1].badges[0].badgeId").value(comeback.getId()))
                .andExpect(jsonPath("$.response.yearlyBadges[1].badges[0].badgeName").value("돌아왔구나"))
                .andExpect(jsonPath("$.response.yearlyBadges[*].badges[*].badgeId",
                        Matchers.not(Matchers.hasItem(oldWeekend.getId().intValue()))));
    }

    @Test
    @DisplayName("대표 뱃지를 설정하지 않았고 보유 뱃지가 없으면 빈 응답을 반환한다.")
    public void readMyBadgesWithoutBadge() throws Exception {
        User user = fixtureSupport.persistUser(UserFixture.createUser());
        String accessToken = testJwtFactory.createAccessToken(user.getId());

        mockMvc.perform(get("/api/v2/users/me/badges")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.representativeBadge").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.response.yearlyBadges").isEmpty());
    }

    @Test
    @DisplayName("보유한 뱃지의 상세 정보를 조회한다.")
    public void readMyBadge() throws Exception {
        User user = fixtureSupport.persistUser(UserFixture.createUser());
        String accessToken = testJwtFactory.createAccessToken(user.getId());
        Badge monthly = saveBadge(user, BadgeType.MONTHLY_50, "10월",
                LocalDate.of(2026, 10, 31), LocalDateTime.of(2026, 10, 31, 22, 0));

        mockMvc.perform(get("/api/v2/users/me/badges/{badgeId}", monthly.getId())
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.badgeId").value(monthly.getId()))
                .andExpect(jsonPath("$.response.badgeName").value("10월 파이터"))
                .andExpect(jsonPath("$.response.badgeImageUrl")
                        .value(String.format(BadgeType.MONTHLY_50.getImageUrl(), 10)))
                .andExpect(jsonPath("$.response.badgeDescription")
                        .value(BadgeType.MONTHLY_50.getDescription()));
    }

    @Test
    @DisplayName("연휴 파이터 뱃지를 상세 조회하면 연휴 종류의 이미지를 반환한다.")
    public void readMyHolidayBadge() throws Exception {
        User user = fixtureSupport.persistUser(UserFixture.createUser());
        String accessToken = testJwtFactory.createAccessToken(user.getId());
        saveHoliday("추석", CHUSEOK_IMAGE_URL, LocalDate.of(2026, 9, 24), LocalDate.of(2026, 9, 28));
        Badge holiday = saveBadge(user, BadgeType.HOLIDAY, "추석",
                LocalDate.of(2026, 9, 24), LocalDateTime.of(2026, 9, 29, 5, 10));

        mockMvc.perform(get("/api/v2/users/me/badges/{badgeId}", holiday.getId())
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.badgeName").value("추석 연휴 파이터"))
                .andExpect(jsonPath("$.response.badgeImageUrl").value(CHUSEOK_IMAGE_URL));
    }

    @Test
    @DisplayName("다른 유저의 뱃지를 상세 조회하면 404를 반환한다.")
    public void readOtherUserBadge() throws Exception {
        User user = fixtureSupport.persistUser(UserFixture.createUser());
        User other = fixtureSupport.persistUser(UserFixture.createUserWithName("다른유저"));
        String accessToken = testJwtFactory.createAccessToken(user.getId());
        Badge otherBadge = saveBadge(other, BadgeType.COMEBACK, null,
                LocalDate.of(2026, 10, 1), LocalDateTime.of(2026, 10, 1, 10, 0));

        mockMvc.perform(get("/api/v2/users/me/badges/{badgeId}", otherBadge.getId())
                        .header("Authorization", accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.code").value("B20000"));
    }

    private void saveHoliday(String name, String badgeImageUrl, LocalDate startDate, LocalDate endDate) {
        HolidayKind holidayKind = holidayKindRepository.save(HolidayKind.builder()
                .name(name)
                .badgeImageUrl(badgeImageUrl)
                .badgeEnabled(true)
                .build());
        holidayRepository.save(Holiday.builder()
                .holidayKind(holidayKind)
                .startDate(startDate)
                .endDate(endDate)
                .build());
    }

    private Badge saveBadge(User user, BadgeType type, String detail, LocalDate date, LocalDateTime createdAt) {
        Badge badge = badgeRepository.save(Badge.builder()
                .userId(user.getId())
                .type(type)
                .detail(detail)
                .date(date)
                .build());
        jdbcTemplate.update("UPDATE badge SET created_at = ? WHERE badge_id = ?", createdAt, badge.getId());
        return badge;
    }
}
