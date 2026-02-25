package com.togedy.togedy_server_v2.domain.support.api;

import com.togedy.togedy_server_v2.domain.support.application.NoticeService;
import com.togedy.togedy_server_v2.domain.support.dto.GetNoticeResponse;
import com.togedy.togedy_server_v2.domain.support.dto.GetNoticesResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/support/notices")
@Tag(name = "Notice", description = "공지사항 APi")
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 전체 조회", description = "공지사항을 전체 조회한다.")
    @GetMapping
    public ApiResponse<List<GetNoticesResponse>> readNotices() {
        List<GetNoticesResponse> responses = noticeService.findNotices();
        return ApiUtil.success(responses);
    }

    @Operation(summary = "공지사항 단일 조회", description = "공지사항을 단일 조회한다.")
    @GetMapping("/{noticeId}")
    public ApiResponse<GetNoticeResponse> readNotice(@PathVariable Long noticeId) {
        GetNoticeResponse response = noticeService.findNotice(noticeId);
        return ApiUtil.success(response);
    }
}
