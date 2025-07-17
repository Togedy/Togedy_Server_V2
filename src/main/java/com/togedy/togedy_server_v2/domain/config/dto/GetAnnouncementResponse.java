package com.togedy.togedy_server_v2.domain.config.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetAnnouncementResponse {

    private boolean hasAnnouncement;
    private String announcement;

    public static GetAnnouncementResponse from(String announcement) {
        return GetAnnouncementResponse.builder()
                .hasAnnouncement(true)
                .announcement(announcement)
                .build();
    }

    public static GetAnnouncementResponse temp() {
        return GetAnnouncementResponse.builder()
                .hasAnnouncement(false)
                .build();
    }
}
