package com.togedy.togedy_server_v2.domain.university.dto;

import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class UniversityDto {

    private Long universityId;
    private String universityName;
    private String universityAdmissionType;
    private int universityAdmissionMethodCount;
    private List<String> addedAdmissionMethodList;

    public static UniversityDto of(
            University university,
            int universityAdmissionMethodCount,
            List<UniversityAdmissionMethod> universityAdmissionMethodList)
    {
        return UniversityDto.builder()
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
