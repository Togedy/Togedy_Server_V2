package com.togedy.togedy_server_v2.domain.support.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetInquiryResponse {

    private boolean hasNext;

    private List<InquiryDto> inquiries;

    public static GetInquiryResponse of(boolean hasNext, List<InquiryDto> inquiries) {
        return GetInquiryResponse.builder()
                .hasNext(hasNext)
                .inquiries(inquiries)
                .build();
    }
}
