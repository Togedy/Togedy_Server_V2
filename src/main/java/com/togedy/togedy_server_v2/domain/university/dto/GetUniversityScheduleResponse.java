package com.togedy.togedy_server_v2.domain.university.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@JsonPropertyOrder({
        "universityName", "admissionType", "isAdded", "admissionList"
})
public class GetUniversityScheduleResponse {

    private String universityName;
    private String universityAdmissionType;
    private List<String> addedUniversityAdmissionMethodList;
    private List<UniversityAdmissionMethodDto> universityAdmissionMethodList;

    public static GetUniversityScheduleResponse of(
            University university,
            List<UniversityAdmissionMethod> addedUniversityAdmissionMethodList,
            List<UniversityAdmissionMethodDto> admissionList)
    {
        return GetUniversityScheduleResponse.builder()
                .universityName(university.getName())
                .universityAdmissionType(university.getAdmissionType())
                .addedUniversityAdmissionMethodList(
                        addedUniversityAdmissionMethodList.stream()
                        .map(UniversityAdmissionMethod::getName)
                                .collect(Collectors.toList()))
                .universityAdmissionMethodList(admissionList)
                .build();
    }
}
