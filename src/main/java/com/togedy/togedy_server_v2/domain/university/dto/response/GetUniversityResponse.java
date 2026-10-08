package com.togedy.togedy_server_v2.domain.university.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUniversityResponse {

    private boolean hasNext;
    private List<UniversityInfo> universityList;

    public static GetUniversityResponse of(boolean hasNext, List<UniversityInfo> universityList) {
        return GetUniversityResponse.builder()
                .hasNext(hasNext)
                .universityList(universityList)
                .build();
    }
}
