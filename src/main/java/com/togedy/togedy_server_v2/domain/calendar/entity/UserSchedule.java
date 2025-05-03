package com.togedy.togedy_server_v2.domain.calendar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status")
    private String status = "CREATED";

    @Column(name = "is_d_day")
    private boolean isDday;

    @Builder
    public UserSchedule(String name,
                        String memo,
                        LocalDate startDate,
                        LocalDate endDate,
                        boolean isDday)
    {
        this.name = name;
        this.memo = memo;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDday = isDday;
    }
}
