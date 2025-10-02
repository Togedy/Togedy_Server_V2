package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetStudyMemberManagementResponse {

    private Long userId;

    private String userName;

    private StudyRole studyRole;

}
