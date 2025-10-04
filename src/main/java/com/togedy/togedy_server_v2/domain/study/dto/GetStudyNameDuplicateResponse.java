package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyNameDuplicateResponse {

    private Boolean isDuplicate;

    public static GetStudyNameDuplicateResponse from(boolean isDuplicate) {
        return GetStudyNameDuplicateResponse.builder()
                .isDuplicate(isDuplicate)
                .build();
    }

}
