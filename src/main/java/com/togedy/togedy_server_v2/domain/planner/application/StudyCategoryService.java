package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.StudyCategoryRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetStudyCategoryResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PatchReorderRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PatchStudyCategoryRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostStudyCategoryRequest;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyCategory;
import com.togedy.togedy_server_v2.domain.planner.exception.DuplicateStudyCategoryException;
import com.togedy.togedy_server_v2.domain.planner.exception.InvalidStudyCategoryReorderException;
import com.togedy.togedy_server_v2.domain.planner.exception.StudyCategoryNotFoundException;
import com.togedy.togedy_server_v2.domain.planner.exception.StudyCategoryNotOwnedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyCategoryService {

    private final StudyCategoryRepository studyCategoryRepository;

    private static final long ORDER_GAP = 1000L;

    /**
     * 스터디 카테고리를 생성한다.
     *
     * @param request   스터디 카테고리 생성 DTO
     * @param userId    유저 ID
     */
    @Transactional
    public void generateStudyCategory(PostStudyCategoryRequest request, Long userId) {
        validateDuplicateOnCreate(request.getCategoryName(), request.getCategoryColor(), userId);

        Long lastOrder = studyCategoryRepository.findMaxOrderIndex(userId);
        Long nextOrder = lastOrder == null ? 1000L : lastOrder + 1000L;

        StudyCategory studyCategory = StudyCategory.builder()
                .userId(userId)
                .name(request.getCategoryName())
                .color(request.getCategoryColor())
                .orderIndex(nextOrder)
                .build();

        studyCategoryRepository.save(studyCategory);
    }

    /**
     * 유저가 보유하고 있는 모든 스터디 카테고리를 조회한다.
     *
     * @param userId    유저ID
     * @return          유저가 보유한 스터디 카테고리 정보 DTO List
     */
    @Transactional(readOnly = true)
    public List<GetStudyCategoryResponse> findAllStudyCategoriesByUserId(Long userId) {
        List<StudyCategory> studyCategoryList = studyCategoryRepository.findAllByUserId(userId);

        return studyCategoryList.stream()
                .map(GetStudyCategoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 유저가 해당 스터디 카테고리의 정보를 수정한다.
     *
     * @param request       카테고리 수정 DTO
     * @param categoryId    수정할 카테고리ID
     * @param userId        유저ID
     */
    @Transactional
    public void modifyStudyCategory(PatchStudyCategoryRequest request, Long categoryId, Long userId) {
        StudyCategory studyCategory = findOwnedCategory(categoryId, userId);

        validateDuplicateOnUpdate(request.getCategoryName(), request.getCategoryColor(), userId, categoryId);

        studyCategory.update(request);
    }

    /**
     * 유저가 해당 스터디 카테고리를 삭제한다.
     *
     * @param categoryId    삭제할 카테고리ID
     * @param userId        유저ID
     */
    @Transactional
    public void removeStudyCategory(Long categoryId, Long userId) {
        StudyCategory studyCategory = findOwnedCategory(categoryId, userId);

        studyCategory.delete();
    }

    /**
     * 유저가 해당 스터디 카테고리의 정렬 순서를 변경한다.
     *
     * @param request       정렬 수정 요청 DTO
     * @param categoryId    정렬 대상 카테고리ID
     * @param userId        유저ID
     */
    @Transactional
    public void reorderStudyCategory(PatchReorderRequest request, Long categoryId, Long userId) {
        validateStudyCategoryReorder(request, categoryId);

        StudyCategory target = findOwnedCategory(categoryId, userId);
        StudyCategory prev = getPrevCategory(request, userId);
        StudyCategory next = getNextCategory(request, userId);

        long newOrderIndex = calculateNewOrderIndex(prev, next);
        target.move(newOrderIndex);
    }

    /**
     * 생성 시에 이름 및 색상이 동일한 스터디 카테고리가 이미 존재하는 지 검증한다.
     *
     * @param categoryName      카테고리명
     * @param categoryColor     카테고리 색상
     * @param userId            유저ID
     */
    private void validateDuplicateOnCreate(String categoryName, String categoryColor, Long userId) {
        if (studyCategoryRepository.existsByNameAndColorAndUserId(categoryName, categoryColor, userId)) {
            throw new DuplicateStudyCategoryException();
        }
    }

    /**
     * 수정 시에 이름 및 색상이 동일한 스터디 카테고리가 이미 존재하는 지 검증한다.
     *
     * @param categoryName      카테고리명
     * @param categoryColor     카테고리 색상
     * @param userId            유저ID
     * @param categoryId        비교 카테고리ID
     */
    private void validateDuplicateOnUpdate(String categoryName, String categoryColor, Long userId, Long categoryId) {
        studyCategoryRepository
                .findByNameAndColorAndUserId(categoryName, categoryColor, userId)
                .filter(category -> !category.getId().equals(categoryId))
                .ifPresent(c -> {
                    throw new DuplicateStudyCategoryException();
                });
    }

    /**
     * 카테고리 정렬 수정의 유효성을 검증한다.
     *
     * @param request       정렬 수정 요청 DTO
     * @param categoryId    정렬 대상 카테고리ID
     */
    private void validateStudyCategoryReorder(PatchReorderRequest request, Long categoryId) {
        if(request.getPrevId() == null && request.getNextId() == null) {
            throw new InvalidStudyCategoryReorderException();
        }

        if (request.getPrevId() != null && request.getPrevId().equals(categoryId)) {
            throw new InvalidStudyCategoryReorderException();
        }
        if (request.getNextId() != null && request.getNextId().equals(categoryId)) {
            throw new InvalidStudyCategoryReorderException();
        }
    }

    private long calculateNewOrderIndex(StudyCategory prev, StudyCategory next) {
        if (prev == null) {
            return next.getOrderIndex() - ORDER_GAP;
        }

        if (next == null) {
            return prev.getOrderIndex() + ORDER_GAP;
        }

        long gap = next.getOrderIndex() - prev.getOrderIndex();
        if (gap <= 1) {
            throw new InvalidStudyCategoryReorderException();
        }

        return prev.getOrderIndex() + gap / 2;
    }

    private StudyCategory findOwnedCategory(Long categoryId, Long userId) {
        StudyCategory category = studyCategoryRepository.findActiveById(categoryId)
                .orElseThrow(StudyCategoryNotFoundException::new);

        if (!category.getUserId().equals(userId)) {
            throw new StudyCategoryNotOwnedException();
        }
        return category;
    }

    private StudyCategory getPrevCategory(PatchReorderRequest request, Long userId) {
        return request.getPrevId() == null
                ? null
                : findOwnedCategory(request.getPrevId(), userId);
    }

    private StudyCategory getNextCategory(PatchReorderRequest request, Long userId) {
        return request.getNextId() == null
                ? null
                : findOwnedCategory(request.getNextId(), userId);
    }

}
