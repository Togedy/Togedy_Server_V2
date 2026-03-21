package com.togedy.togedy_server_v2.domain.support.dto;

import com.togedy.togedy_server_v2.domain.support.entity.Notice;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetNoticeResponse {

    private String noticeTitle;

    private LocalDate publishedAt;

    private String noticeContent;

    public static GetNoticeResponse from(Notice notice) {
        return GetNoticeResponse.builder()
                .noticeTitle(notice.getTitle())
                .publishedAt(notice.getCreatedAt().toLocalDate())
                .noticeContent(notice.getContent())
                .build();
    }
}
