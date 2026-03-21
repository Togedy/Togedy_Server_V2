package com.togedy.togedy_server_v2.domain.support.dto;

import com.togedy.togedy_server_v2.domain.support.enums.InquiryType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostInquiryRequest {

    private InquiryType inquiryType;

    private String inquiryContent;

    private String replyEmail;

}
