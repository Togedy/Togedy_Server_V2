package com.togedy.togedy_server_v2.domain.badge.entity;

import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Builder
    public HolidayKind(String name, String badgeImageUrl, boolean badgeEnabled) {
        this.name = name;
        this.badgeImageUrl = badgeImageUrl;
        this.badgeEnabled = badgeEnabled;
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
