package com.togedy.togedy_server_v2.domain.schedule.entity;

import lombok.Getter;

@Getter
public enum ScheduleType {
    USER, UNIVERSITY;

    private String scheduleType;
}
