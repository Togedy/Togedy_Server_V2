package com.togedy.togedy_server_v2.domain.calendar.application;

import com.togedy.togedy_server_v2.domain.calendar.Exception.CategoryNotFoundException;
import com.togedy.togedy_server_v2.domain.calendar.Exception.CategoryNotOwnedException;
import com.togedy.togedy_server_v2.domain.calendar.dao.CategoryRepository;
import com.togedy.togedy_server_v2.domain.calendar.dto.GetCategoryResponse;
import com.togedy.togedy_server_v2.domain.calendar.dto.PatchCategoryRequest;
import com.togedy.togedy_server_v2.domain.calendar.dto.PostCategoryRequest;
import com.togedy.togedy_server_v2.domain.calendar.entity.Category;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void generateCategory(PostCategoryRequest request) {
        // 더미 유저, 이후 변경
        User user = new User("dummy", "dummy");
        Category category = new Category(user, request.getCategoryName(), request.getCategoryColor());
        categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<GetCategoryResponse> findAllCategoriesByUserId(Long userId) {
        List<Category> categoryList = categoryRepository.findAllByUserId(userId);

        return categoryList.stream()
                .map(GetCategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void modifyCategory(PatchCategoryRequest request, Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryNotOwnedException();
        }

        category.update(request);
    }

    public void removeCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryNotOwnedException();
        }

        categoryRepository.delete(category);
    }
}
