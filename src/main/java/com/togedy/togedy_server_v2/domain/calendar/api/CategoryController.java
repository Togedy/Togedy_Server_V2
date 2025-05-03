package com.togedy.togedy_server_v2.domain.calendar.api;

import com.togedy.togedy_server_v2.domain.calendar.application.CategoryService;
import com.togedy.togedy_server_v2.domain.calendar.dto.PostCategoryRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/calendars/categories")
public class CategoryController {

    private CategoryService categoryService;

    @PostMapping("")
    public ApiResponse<Void> createCategory(@RequestBody PostCategoryRequest request) {
        categoryService.generateCategory(request);
        return ApiUtil.successOnly();
    }
}
