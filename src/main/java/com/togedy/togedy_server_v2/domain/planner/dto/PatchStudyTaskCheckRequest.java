package com.togedy.togedy_server_v2.domain.planner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchStudyTaskCheckRequest {
    @JsonProperty("isChecked")
    @Schema(description = "변경할 태스크 완료 여부", type = "boolean", example = "true")
    private boolean checked;
}
