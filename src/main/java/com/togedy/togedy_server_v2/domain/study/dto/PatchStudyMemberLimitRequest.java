package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchStudyMemberLimitRequest {

    private Integer studyMemberLimit;

}
