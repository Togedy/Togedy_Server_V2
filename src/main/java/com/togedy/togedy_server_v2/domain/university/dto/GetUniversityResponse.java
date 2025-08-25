package com.togedy.togedy_server_v2.domain.university.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetUniversityResponse {

    private boolean hasNext;
    private List<UniversityDto> universityList;

    public static GetUniversityResponse of(boolean hasNext, List<UniversityDto> universityList) {
        return GetUniversityResponse.builder()
                .hasNext(hasNext)
                .universityList(universityList)
                .build();
    }
}
