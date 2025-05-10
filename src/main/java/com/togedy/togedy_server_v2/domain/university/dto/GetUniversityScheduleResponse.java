package com.togedy.togedy_server_v2.domain.university.dto;

import com.togedy.togedy_server_v2.domain.university.entity.University;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetUniversityScheduleResponse {

    private String universityName;

    private String admissionType;

    private List<AdmissionTypeDto> admissionList;

    public static GetUniversityScheduleResponse of(University university, List<AdmissionTypeDto> admissionList) {
        return GetUniversityScheduleResponse.builder()
                .universityName(university.getName())
                .admissionType(university.getAdmissionType())
                .admissionList(admissionList)
                .build();
    }
}
