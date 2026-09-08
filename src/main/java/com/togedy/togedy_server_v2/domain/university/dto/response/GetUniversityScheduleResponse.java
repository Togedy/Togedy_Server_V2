package com.togedy.togedy_server_v2.domain.university.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import com.togedy.togedy_server_v2.domain.university.enums.AdmissionType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonPropertyOrder({
        "universityName", "admissionType", "isAdded", "admissionList"
})
public class GetUniversityScheduleResponse {

    private String universityName;
    private AdmissionType universityAdmissionType;
    private List<String> addedUniversityAdmissionMethodList;
    private List<UniversityAdmissionMethodInfo> universityAdmissionMethodList;

    public static GetUniversityScheduleResponse of(
            University university,
            List<UniversityAdmissionMethod> addedUniversityAdmissionMethodList,
            List<UniversityAdmissionMethodInfo> admissionList
    ) {
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
