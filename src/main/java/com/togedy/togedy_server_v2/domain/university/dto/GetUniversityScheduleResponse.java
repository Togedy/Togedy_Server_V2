package com.togedy.togedy_server_v2.domain.university.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.togedy.togedy_server_v2.domain.university.entity.University;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonPropertyOrder({
        "universityName", "admissionType", "isAdded", "admissionList"
})
public class GetUniversityScheduleResponse {

    private String universityName;
    private String admissionType;
    @JsonProperty("isAdded")
    private boolean added;
    private List<AdmissionTypeDto> admissionList;

    public static GetUniversityScheduleResponse of(University university,
                                                   List<AdmissionTypeDto> admissionList,
                                                   boolean isAdded) {
        return GetUniversityScheduleResponse.builder()
                .universityName(university.getName())
                .admissionType(university.getAdmissionType())
                .added(isAdded)
                .admissionList(admissionList)
                .build();
    }
}
