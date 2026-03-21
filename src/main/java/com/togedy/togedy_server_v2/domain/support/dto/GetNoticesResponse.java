package com.togedy.togedy_server_v2.domain.support.dto;

import com.togedy.togedy_server_v2.domain.support.entity.Notice;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetNoticesResponse {

    private Long noticeId;

    private String noticeTitle;

    private LocalDate publishedAt;

    public static GetNoticesResponse from(Notice notice) {
        return GetNoticesResponse.builder()
                .noticeId(notice.getId())
                .noticeTitle(notice.getTitle())
                .publishedAt(notice.getCreatedAt().toLocalDate())
                .build();
    }
}
