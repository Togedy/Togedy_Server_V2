package com.togedy.togedy_server_v2.domain.study.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchPlannerVisibilityRequest {

    @JsonProperty("isPlannerVisible")
    private boolean plannerVisible;

}
