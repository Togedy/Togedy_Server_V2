package com.togedy.togedy_server_v2.domain.schedule.entity;

import com.togedy.togedy_server_v2.domain.schedule.dto.PatchUserScheduleRequest;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "user_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_schedule_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "memo")
    private String memo;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "is_d_day", nullable = false)
    private boolean dDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public UserSchedule(User user,
                        Category category,
                        String name,
                        String memo,
                        LocalDate startDate,
                        LocalTime startTime,
                        LocalDate endDate,
                        LocalTime endTime,
                        boolean dDay) {
        this.user = user;
        this.category = category;
        this.name = name;
        this.memo = memo;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.dDay = dDay;
    }

    public void update(PatchUserScheduleRequest request) {
        if (request.getUserScheduleName() != null) {
            this.name = request.getUserScheduleName();
        }
        if (request.getMemo() != null) {
            this.memo = request.getMemo();
        }
        if (request.getStartDate() != null) {
            this.startDate = LocalDateTime.parse(request.getStartDate());
        }
        if (request.getAllDayStart() != null) {
            this.startDate = LocalDateTime.parse(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            this.startDate = LocalDateTime.parse(request.getStartDate());
        }
        if (request.getAllDayEnd() != null) {
            this.startDate = LocalDateTime.parse(request.getStartDate());
        }
    }

    public void update(Category category) {
        this.category = category;
    }
}
