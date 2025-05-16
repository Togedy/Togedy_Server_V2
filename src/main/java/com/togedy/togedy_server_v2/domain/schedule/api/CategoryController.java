package com.togedy.togedy_server_v2.domain.schedule.api;

import com.togedy.togedy_server_v2.domain.schedule.application.CategoryService;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetCategoryResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.PatchCategoryRequest;
import com.togedy.togedy_server_v2.domain.schedule.dto.PostCategoryRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
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
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("")
    public ApiResponse<Void> createCategory(@RequestBody PostCategoryRequest request,
                                            @AuthenticationPrincipal AuthUser user) {
        categoryService.generateCategory(request, user.getId());
        return ApiUtil.successOnly();
    }

    @GetMapping("")
    public ApiResponse<List<GetCategoryResponse>> readAllCategories(Long userId) {
        List<GetCategoryResponse> response = categoryService.findAllCategoriesByUserId(userId);
        return ApiUtil.success(response);
    }

    @PatchMapping("/{categoryId}")
    public ApiResponse<Void> updateCategory(@RequestBody PatchCategoryRequest request,
                                            @PathVariable Long categoryId,
                                            Long userId) {
        categoryService.modifyCategory(request, categoryId, userId);
        return ApiUtil.successOnly();
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long categoryId, Long userId) {
        categoryService.removeCategory(categoryId, userId);
        return ApiUtil.successOnly();
    }

}
