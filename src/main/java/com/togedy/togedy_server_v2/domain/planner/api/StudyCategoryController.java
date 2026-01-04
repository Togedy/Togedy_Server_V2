package com.togedy.togedy_server_v2.domain.planner.api;

import com.togedy.togedy_server_v2.domain.planner.application.StudyCategoryService;
import com.togedy.togedy_server_v2.domain.planner.dto.GetStudyCategoryResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PostStudyCategoryRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "스터디 카테고리 조회", description = "해당 유저가 보유한 카테고리를 조회한다.")
    @GetMapping("")
    public ApiResponse<List<GetStudyCategoryResponse>> readAllStudyCategories(@AuthenticationPrincipal AuthUser user) {
        List<GetStudyCategoryResponse> response = studyCategoryService.findAllStudyCategoriesByUserId(user.getId());
        return ApiUtil.success(response);
    }
}
