package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchStudyMemberLimitRequest {

    private Integer studyMemberLimit;

}
