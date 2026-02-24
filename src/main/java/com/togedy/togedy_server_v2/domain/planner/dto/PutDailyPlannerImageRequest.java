package com.togedy.togedy_server_v2.domain.planner.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PutDailyPlannerImageRequest {

    private MultipartFile plannerImage;

    private boolean removePlannerImage;
}
