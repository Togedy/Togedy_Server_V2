package com.togedy.togedy_server_v2.domain.schedule.entity;

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

@Entity
@Table(name = "user_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_schedule_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "memo")
    private String memo;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_all_day_start")
    private boolean allDayStart;

    @Column(name = "is_all_day_end")
    private boolean allDayEnd;

    @Column(name = "is_d_day")
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
                        LocalDateTime startDate,
                        boolean allDayStart,
                        LocalDateTime endDate,
                        boolean allDayEnd,
                        boolean dDay) {

        this.user = user;
        this.category = category;
        this.name = name;
        this.memo = memo;
        this.startDate = startDate;
        this.allDayStart = allDayStart;
        this.endDate = endDate;
        this.allDayEnd = allDayEnd;
        this.dDay = dDay;
    }

}
