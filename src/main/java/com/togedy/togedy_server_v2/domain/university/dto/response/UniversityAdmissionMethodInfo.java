package com.togedy.togedy_server_v2.domain.university.dto.response;

import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UniversityAdmissionMethodInfo {

    private String universityAdmissionMethod;
    private Long universityAdmissionMethodId;
    private List<UniversityScheduleInfo> universityScheduleList;

    public static UniversityAdmissionMethodInfo of(
            UniversityAdmissionMethod universityAdmissionMethod,
            List<UniversityScheduleInfo> universityScheduleList
    ) {
        return UniversityAdmissionMethodInfo.builder()
                .universityAdmissionMethod(universityAdmissionMethod.getName())
                .universityAdmissionMethodId(universityAdmissionMethod.getId())
                .universityScheduleList(universityScheduleList)
                .build();
    }
}
