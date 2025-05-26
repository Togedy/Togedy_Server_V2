package com.togedy.togedy_server_v2.domain.schedule.application;

import com.togedy.togedy_server_v2.domain.schedule.Exception.CategoryNotFoundException;
import com.togedy.togedy_server_v2.domain.schedule.Exception.CategoryNotOwnedException;
import com.togedy.togedy_server_v2.domain.schedule.Exception.DuplicateCategoryException;
import com.togedy.togedy_server_v2.domain.schedule.dao.CategoryRepository;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetCategoryResponse;
import com.togedy.togedy_server_v2.domain.schedule.dto.PatchCategoryRequest;
import com.togedy.togedy_server_v2.domain.schedule.dto.PostCategoryRequest;
import com.togedy.togedy_server_v2.domain.schedule.entity.Category;
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

    /**
     * 카테고리를 생성한다.
     *
     * @param request   카테고리 생성 DTO
     * @param userId    유저ID
     */
    @Transactional
    public void generateCategory(PostCategoryRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        if (categoryRepository.existsByColorAndNameAndUser(request.getCategoryName(), request.getCategoryColor(), user)) {
            throw new DuplicateCategoryException();
        }

        Category category = new Category(user, request.getCategoryName(), request.getCategoryColor());
        categoryRepository.save(category);
    }

    /**
     * 유저가 보유하고 있는 모든 카테고리를 조회한다.
     *
     * @param userId    유저ID
     * @return          유저가 보유한 카테고리 정보 DTO List
     */
    @Transactional(readOnly = true)
    public List<GetCategoryResponse> findAllCategoriesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);
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

        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryNotOwnedException();
        }

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
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryNotOwnedException();
        }

        categoryRepository.delete(category);
    }
}
