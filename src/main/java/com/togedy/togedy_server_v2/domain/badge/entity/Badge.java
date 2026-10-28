package com.togedy.togedy_server_v2.domain.badge.entity;

import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "badge", uniqueConstraints = @UniqueConstraint(
        name = "uk_badge_user_id_type_period_key",
        columnNames = {"user_id", "type", "period_key"})
)
public class Badge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "detail", updatable = false)
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, columnDefinition = "varchar(30)")
    private BadgeType type;

    @Column(name = "period_key", nullable = false, updatable = false)
    private String periodKey;

    @Builder
    public Badge(Long userId, String detail, BadgeType type, LocalDate date) {
        this.userId = userId;
        this.detail = detail;
        this.type = type;
        this.periodKey = type.getPeriodType().toKey(date);
    }
}
