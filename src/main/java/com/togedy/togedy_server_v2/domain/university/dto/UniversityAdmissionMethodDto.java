package com.togedy.togedy_server_v2.domain.university.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UniversityAdmissionMethodDto {

    private String universityAdmissionMethod;
    private Long universityAdmissionMethodId;
    private List<UniversityScheduleDto> universityScheduleList;

    public static UniversityAdmissionMethodDto of(UniversityAdmissionMethod universityAdmissionMethod, List<UniversityScheduleDto> universityScheduleList) {
        return UniversityAdmissionMethodDto.builder()
                .universityAdmissionMethod(universityAdmissionMethod.getName())
                .universityAdmissionMethodId(universityAdmissionMethod.getId())
                .universityScheduleList(universityScheduleList)
                .build();
    }
}
