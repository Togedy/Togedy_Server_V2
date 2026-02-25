package com.togedy.togedy_server_v2.domain.support.api;

import com.togedy.togedy_server_v2.domain.support.application.InquiryService;
import com.togedy.togedy_server_v2.domain.support.dto.GetInquiryResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/support/inquiries")
@Tag(name = "Admin Inquiry", description = "관리자 문의 API")
public class AdminInquiryController {

    private final InquiryService inquiryService;

    @Operation(summary = "문의 조회 (관리자)", description = "관리자가 문의를 조회한다.")
    @GetMapping
    public ApiResponse<GetInquiryResponse> readInquiries(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        GetInquiryResponse response = inquiryService.findInquiries(page, size);
        return ApiUtil.success(response);
    }
}
