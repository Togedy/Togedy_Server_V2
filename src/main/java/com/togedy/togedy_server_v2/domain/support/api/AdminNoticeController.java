package com.togedy.togedy_server_v2.domain.support.api;

import com.togedy.togedy_server_v2.domain.support.application.NoticeService;
import com.togedy.togedy_server_v2.domain.support.dto.PatchNoticeRequest;
import com.togedy.togedy_server_v2.domain.support.dto.PostNoticeRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/support/notices")
@Tag(name = "Admin Notice", description = "관리자 공지사항 API")
public class AdminNoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 추가 (관리자)", description = "관리자가 공지사항을 추가한다.")
    @PostMapping
    public ApiResponse<Void> createNotice(
            @RequestBody PostNoticeRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        noticeService.generateNotice(request, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "공지사항 수정 (관리자)", description = "관리자가 공지사항을 수정한다.")
    @PatchMapping("/{noticeId}")
    public ApiResponse<Void> updateNotice(
            @RequestBody PatchNoticeRequest request,
            @PathVariable Long noticeId
    ) {
        noticeService.modifyNotice(request, noticeId);
        return ApiUtil.successOnly();
    }

    @Operation(summary = "공지사항 제거 (관리자)", description = "관리자가 공지사항을 제거한다.")
    @DeleteMapping("/{noticeId}")
    public ApiResponse<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.removeNotice(noticeId);
        return ApiUtil.successOnly();
    }
}
