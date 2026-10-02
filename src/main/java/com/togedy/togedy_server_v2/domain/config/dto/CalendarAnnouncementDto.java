package com.togedy.togedy_server_v2.domain.config.dto;

import com.togedy.togedy_server_v2.domain.config.entity.CalendarAnnouncement;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CalendarAnnouncementDto {

    private Long calendarAnnouncementId;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static CalendarAnnouncementDto from(CalendarAnnouncement calendarAnnouncement) {
        return CalendarAnnouncementDto.builder()
                .calendarAnnouncementId(calendarAnnouncement.getId())
                .content(calendarAnnouncement.getContent())
                .createdAt(calendarAnnouncement.getCreatedAt())
                .updatedAt(calendarAnnouncement.getUpdatedAt())
                .build();
    }
}
