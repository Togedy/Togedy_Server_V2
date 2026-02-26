package com.togedy.togedy_server_v2.domain.support.dto;

import com.togedy.togedy_server_v2.domain.support.entity.Inquiry;
import com.togedy.togedy_server_v2.domain.support.enums.InquiryType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InquiryDto {

    private Long inquiryId;

    private InquiryType inquiryType;

    private String inquiryContent;

    private String inquiryStatus;

    private String replyEmail;

    public static InquiryDto from(Inquiry inquiry) {
        return InquiryDto.builder()
                .inquiryId(inquiry.getId())
                .inquiryType(inquiry.getType())
                .inquiryContent(inquiry.getContent())
                .inquiryStatus(inquiry.getStatus().getDescription())
                .replyEmail(inquiry.getReplyEmail())
                .build();
    }
}
