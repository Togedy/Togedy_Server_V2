package com.togedy.togedy_server_v2.domain.badge.entity;

import com.togedy.togedy_server_v2.domain.badge.enums.DateRuleType;
import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "holiday_kind", uniqueConstraints = @UniqueConstraint(
        name = "uk_holiday_kind_name",
        columnNames = "name")
)
public class HolidayKind extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holiday_kind_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "badge_image_url")
    private String badgeImageUrl;

    @Column(name = "badge_enabled", nullable = false)
    private boolean badgeEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "date_rule_type", nullable = false, columnDefinition = "varchar(20)")
    private DateRuleType dateRuleType;

    // 날짜 규칙의 월·일 (양력 또는 음력), MANUAL이면 null
    @Column(name = "rule_month")
    private Integer ruleMonth;

    @Column(name = "rule_day")
    private Integer ruleDay;

    // 규칙 날짜 앞뒤로 연휴에 포함하는 일수 (예: 설날은 음력 1/1 앞뒤 하루씩)
    @Column(name = "days_before", nullable = false)
    private int daysBefore;

    @Column(name = "days_after", nullable = false)
    private int daysAfter;

    /**
     * 연휴 종류를 생성한다. 날짜 규칙을 지정하지 않으면 어드민이 날짜를 직접 지정하는 MANUAL 종류가 된다.
     */
    @Builder
    public HolidayKind(String name, String badgeImageUrl, boolean badgeEnabled, DateRuleType dateRuleType,
                       Integer ruleMonth, Integer ruleDay, int daysBefore, int daysAfter) {
        this.name = name;
        this.badgeImageUrl = badgeImageUrl;
        this.badgeEnabled = badgeEnabled;
        this.dateRuleType = dateRuleType == null ? DateRuleType.MANUAL : dateRuleType;
        this.ruleMonth = ruleMonth;
        this.ruleDay = ruleDay;
        this.daysBefore = daysBefore;
        this.daysAfter = daysAfter;
    }

    /**
     * 규칙 날짜 앞뒤로 여러 날을 포함하는 연휴 기간(설날, 추석)인지 확인한다.
     *
     * @return 연휴 기간이면 {@code true}
     */
    public boolean isLongHoliday() {
        return daysBefore > 0 || daysAfter > 0;
    }

    public void update(String name, boolean badgeEnabled) {
        this.name = name;
        this.badgeEnabled = badgeEnabled;
    }

    public String changeBadgeImageUrl(String badgeImageUrl) {
        String oldBadgeImageUrl = this.badgeImageUrl;
        this.badgeImageUrl = badgeImageUrl;
        return oldBadgeImageUrl;
    }
}
