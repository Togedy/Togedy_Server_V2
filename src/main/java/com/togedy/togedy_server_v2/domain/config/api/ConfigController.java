package com.togedy.togedy_server_v2.domain.config.api;

import com.togedy.togedy_server_v2.domain.config.application.ConfigService;
import com.togedy.togedy_server_v2.domain.config.dto.GetAnnouncementResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
@Tag(name = "Config", description = "앱 설정 API")
public class ConfigController {

    private final ConfigService configService;

    @Operation(summary = "공지사항 조회", description = "공지사항을 조회한다.")
    @GetMapping("/calendar/announcement")
    public ApiResponse<GetAnnouncementResponse> readAnnouncement() {
        GetAnnouncementResponse response = configService.findAnnouncement();
        return ApiUtil.success(response);
    }
}
