package com.togedy.togedy_server_v2.domain.schedule.application;

import com.togedy.togedy_server_v2.domain.schedule.exception.CategoryNotFoundException;
import com.togedy.togedy_server_v2.domain.schedule.exception.CategoryNotOwnedException;
import com.togedy.togedy_server_v2.domain.schedule.exception.DuplicateCategoryException;
import com.togedy.togedy_server_v2.domain.schedule.dao.CategoryRepository;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetCategoryResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.PatchCategoryRequest;
import com.togedy.togedy_server_v2.domain.schedule.dto.PostCategoryRequest;
import com.togedy.togedy_server_v2.domain.schedule.entity.Category;
import com.togedy.togedy_server_v2.domain.user.application.UserService;
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
    private final UserService userService;

    /**
     * 카테고리를 생성한다.
     *
     * @param request   카테고리 생성 DTO
     * @param userId    유저ID
     */
    @Transactional
    public void generateCategory(PostCategoryRequest request, Long userId) {
        User user = userService.loadUserById(userId);

        validateDuplicateCategory(request.getCategoryName(), request.getCategoryColor(), userId);

        Category category = new Category(user, request.getCategoryName(), request.getCategoryColor());
        categoryRepository.save(category);
    }

    /**
     * 유저가 보유하고 있는 모든 카테고리를 조회한다.
     *
     * @param userId    유저ID
     * @return          유저가 보유한 카테고리 정보 DTO List
     */
    public List<GetCategoryResponse> findAllCategoriesByUserId(Long userId) {
        User user = userService.loadUserById(userId);

        List<Category> categoryList = categoryRepository.findAllByUserId(userId);

        return categoryList.stream()
                .map(GetCategoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 유저가 보유 중인 카테고리의 정보를 수정한다.
     *
     * @param request       카테고리 수정 DTO
     * @param categoryId    수정할 카테고리ID
     * @param userId        유저ID
     */
    @Transactional
    public void modifyCategory(PatchCategoryRequest request, Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
        User user = userService.loadUserById(userId);

        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryNotOwnedException();
        }

        validateDuplicateCategory(request.getCategoryName(), request.getCategoryColor(), userId);

        category.update(request);
    }

    /**
     * 유저가 보유 중인 카테고리를 제거한다.
     *
     * @param categoryId    제거할 카테고리ID
     * @param userId        유저ID
     */
    @Transactional
    public void removeCategory(Long categoryId, Long userId) {
        User user = userService.loadUserById(userId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryNotOwnedException();
        }

        categoryRepository.delete(category);
    }

    private void validateDuplicateCategory(String categoryName, String categoryColor, Long userId) {
        if (categoryRepository.existsByNameAndColorAndUserId(categoryName, categoryColor, userId)) {
            throw new DuplicateCategoryException();
        }
    }
}
