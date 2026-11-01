package com.togedy.togedy_server_v2.domain.badge.entity;

import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "holiday", uniqueConstraints = @UniqueConstraint(
        name = "uk_holiday_start_date",
        columnNames = "start_date")
)
public class Holiday extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holiday_id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "holiday_kind_id", nullable = false)
    private HolidayKind holidayKind;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder
    public Holiday(HolidayKind holidayKind, LocalDate startDate, LocalDate endDate) {
        this.holidayKind = holidayKind;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void update(HolidayKind holidayKind, LocalDate startDate, LocalDate endDate) {
        this.holidayKind = holidayKind;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isEnded(LocalDate studyDate) {
        return endDate.isBefore(studyDate);
    }
}
