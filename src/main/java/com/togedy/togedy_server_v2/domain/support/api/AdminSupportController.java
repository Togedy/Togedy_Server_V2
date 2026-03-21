package com.togedy.togedy_server_v2.domain.support.api;

import com.togedy.togedy_server_v2.domain.support.application.InquiryService;
import com.togedy.togedy_server_v2.domain.support.application.NoticeService;
import com.togedy.togedy_server_v2.domain.support.dto.GetInquiryResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/support")
@Tag(name = "Admin Support", description = "관리자 고객 지원 API")
public class AdminSupportController {

    private final NoticeService noticeService;
    private final InquiryService inquiryService;

    @Operation(summary = "문의 조회 (관리자)", description = "관리자가 문의를 조회한다.")
    @GetMapping("/inquiries")
    public ApiResponse<GetInquiryResponse> readInquiries(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        GetInquiryResponse response = inquiryService.findInquiries(page, size);
        return ApiUtil.success(response);
    }

    @Operation(summary = "공지사항 추가 (관리자)", description = "관리자가 공지사항을 추가한다.")
    @PostMapping("/notices")
    public ApiResponse<Void> createNotice(
            @RequestBody PostNoticeRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        noticeService.generateNotice(request, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "공지사항 수정 (관리자)", description = "관리자가 공지사항을 수정한다.")
    @PatchMapping("/notices/{noticeId}")
    public ApiResponse<Void> updateNotice(
            @RequestBody PatchNoticeRequest request,
            @PathVariable Long noticeId
    ) {
        noticeService.modifyNotice(request, noticeId);
        return ApiUtil.successOnly();
    }

    @Operation(summary = "공지사항 제거 (관리자)", description = "관리자가 공지사항을 제거한다.")
    @DeleteMapping("/notices/{noticeId}")
    public ApiResponse<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.removeNotice(noticeId);
        return ApiUtil.successOnly();
    }
}
