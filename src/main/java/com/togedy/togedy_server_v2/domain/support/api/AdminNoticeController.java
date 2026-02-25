package com.togedy.togedy_server_v2.domain.support.api;

import com.togedy.togedy_server_v2.domain.support.application.NoticeService;
import com.togedy.togedy_server_v2.domain.support.dto.PatchNoticeRequest;
import com.togedy.togedy_server_v2.domain.support.dto.PostNoticeRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
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
public class AdminNoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ApiResponse<Void> createNotice(
            @RequestBody PostNoticeRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        noticeService.generateNotice(request, user.getId());
        return ApiUtil.successOnly();
    }

    @PatchMapping("/{noticeId}")
    public ApiResponse<Void> updateNotice(
            @RequestBody PatchNoticeRequest request,
            @PathVariable Long noticeId,
            @AuthenticationPrincipal AuthUser user
    ) {
        noticeService.modifyNotice(request, noticeId, user.getId());
        return ApiUtil.successOnly();
    }

    @DeleteMapping("/{noticeId}")
    public ApiResponse<Void> deleteNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal AuthUser user
    ) {
        noticeService.removeNotice(noticeId, user.getId());
        return ApiUtil.successOnly();
    }
}
