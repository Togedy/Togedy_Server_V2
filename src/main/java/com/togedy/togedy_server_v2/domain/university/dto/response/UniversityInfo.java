package com.togedy.togedy_server_v2.domain.university.dto.response;

import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import com.togedy.togedy_server_v2.domain.university.enums.AdmissionType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UniversityInfo {

    private Long universityId;
    private String universityName;
    private AdmissionType universityAdmissionType;
    private int universityAdmissionMethodCount;
    private List<String> addedAdmissionMethodList;

    public static UniversityInfo of(
            University university,
            int universityAdmissionMethodCount,
            List<UniversityAdmissionMethod> universityAdmissionMethodList
    ) {
        return UniversityInfo.builder()
                .universityId(university.getId())
                .universityName(university.getName())
                .universityAdmissionType(university.getAdmissionType())
                .universityAdmissionMethodCount(universityAdmissionMethodCount)
                .addedAdmissionMethodList(
                        universityAdmissionMethodList.stream()
                                .map(UniversityAdmissionMethod::getName)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
