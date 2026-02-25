package com.togedy.togedy_server_v2.domain.support.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostNoticeRequest {

    private String noticeTitle;

    private String noticeContent;

}
