package com.togedy.togedy_server_v2.domain.planner.api;

import com.togedy.togedy_server_v2.domain.planner.application.StudyCategoryService;
import com.togedy.togedy_server_v2.domain.planner.dto.PostStudyCategoryRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/planners/categories")
public class StudyCategoryController {

    private final StudyCategoryService studyCategoryService;

    @Operation(summary = "스터디 카테고리 생성", description = "스터디 카테고리를 생성한다.")
    @PostMapping("")
    public ApiResponse<Void> createStudyCategory(@RequestBody PostStudyCategoryRequest request,
                                                 @AuthenticationPrincipal AuthUser user) {
        studyCategoryService.generateStudyCategory(request, user.getId());
        return ApiUtil.successOnly();
    }
}
