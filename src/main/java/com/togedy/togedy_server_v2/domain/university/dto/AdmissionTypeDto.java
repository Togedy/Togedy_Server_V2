package com.togedy.togedy_server_v2.domain.university.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdmissionTypeDto {

    private String admissionMethod;
    private List<UniversityScheduleDto> universityScheduleList;

    public static AdmissionTypeDto of(String admissionMethod, List<UniversityScheduleDto> universityScheduleList) {
        return AdmissionTypeDto.builder()
                .admissionMethod(admissionMethod)
                .universityScheduleList(universityScheduleList)
                .build();
    }
}
