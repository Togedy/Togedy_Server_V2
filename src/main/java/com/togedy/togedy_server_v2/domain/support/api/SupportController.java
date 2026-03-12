package com.togedy.togedy_server_v2.domain.support.api;

import com.togedy.togedy_server_v2.domain.support.application.InquiryService;
import com.togedy.togedy_server_v2.domain.support.application.NoticeService;
import com.togedy.togedy_server_v2.domain.support.dto.GetNoticeResponse;
import com.togedy.togedy_server_v2.domain.support.dto.GetNoticesResponse;
import com.togedy.togedy_server_v2.domain.support.dto.PostInquiryRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/support")
@Tag(name = "Support", description = "고객 지원 API")
public class SupportController {

    private final NoticeService noticeService;
    private final InquiryService inquiryService;

    @Operation(summary = "공지사항 전체 조회", description = "공지사항을 전체 조회한다.")
    @GetMapping("/notices")
    public ApiResponse<List<GetNoticesResponse>> readNotices() {
        List<GetNoticesResponse> responses = noticeService.findNotices();
        return ApiUtil.success(responses);
    }

    @Operation(summary = "공지사항 단일 조회", description = "공지사항을 단일 조회한다.")
    @GetMapping("/notices/{noticeId}")
    public ApiResponse<GetNoticeResponse> readNotice(@PathVariable Long noticeId) {
        GetNoticeResponse response = noticeService.findNotice(noticeId);
        return ApiUtil.success(response);
    }

    @Operation(summary = "문의하기", description = "문의를 추가한다.")
    @PostMapping("/inquiries")
    public ApiResponse<Void> createInquiry(
            @RequestBody PostInquiryRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        inquiryService.generateInquiry(request, user.getId());
        return ApiUtil.successOnly();
    }
}
