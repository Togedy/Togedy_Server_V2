package com.togedy.togedy_server_v2.domain.schedule.api;

import com.togedy.togedy_server_v2.domain.schedule.application.CategoryService;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetCategoryResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.PatchCategoryRequest;
import com.togedy.togedy_server_v2.domain.schedule.dto.PostCategoryRequest;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/calendars/categories")
@Tag(name = "Category", description = "카테고리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성", description = "카테고리를 생성한다.")
    @PostMapping("")
    public ApiResponse<Void> createCategory(@RequestBody PostCategoryRequest request,
                                            @AuthenticationPrincipal AuthUser user) {
        categoryService.generateCategory(request, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "카테고리 조회", description = "해당 유저가 생성한 카테고리를 조회한다.")
    @GetMapping("")
    public ApiResponse<List<GetCategoryResponse>> readAllCategories(@AuthenticationPrincipal AuthUser user) {
        List<GetCategoryResponse> response = categoryService.findAllCategoriesByUserId(user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "카테고리 수정", description = "해당 카테고리의 정보를 수정한다.")
    @PatchMapping("/{categoryId}")
    public ApiResponse<Void> updateCategory(@RequestBody PatchCategoryRequest request,
                                            @PathVariable Long categoryId,
                                            @AuthenticationPrincipal AuthUser user) {
        categoryService.modifyCategory(request, categoryId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "카테고리 제거", description = "해당 카테고리를 제거한다.")
    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long categoryId,
                                            @AuthenticationPrincipal AuthUser user) {
        categoryService.removeCategory(categoryId, user.getId());
        return ApiUtil.successOnly();
    }

}
