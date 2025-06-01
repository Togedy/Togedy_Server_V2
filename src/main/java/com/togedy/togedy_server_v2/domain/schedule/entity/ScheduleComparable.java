package com.togedy.togedy_server_v2.domain.schedule.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ScheduleComparable {
    LocalDate getStartDate();
    LocalTime getStartTime();
    LocalDate   getEndDate();
    LocalTime   getEndTime();
}
