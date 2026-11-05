package com.togedy.togedy_server_v2.domain.badge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatchRepresentativeBadgeRequest {

    @NotNull(message = "뱃지 ID는 필수 항목입니다.")
    private Long badgeId;
}
