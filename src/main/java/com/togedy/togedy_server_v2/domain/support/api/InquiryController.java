package com.togedy.togedy_server_v2.domain.support.api;

import com.togedy.togedy_server_v2.domain.support.application.InquiryService;
import com.togedy.togedy_server_v2.domain.support.dto.PostInquiryRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/support/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ApiResponse<Void> createInquiry(
            @RequestBody PostInquiryRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        inquiryService.generateInquiry(request, user.getId());
        return ApiUtil.successOnly();
    }
}
