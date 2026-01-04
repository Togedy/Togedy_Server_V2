package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.StudyCategoryRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetStudyCategoryResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PatchStudyCategoryRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostStudyCategoryRequest;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyCategory;
import com.togedy.togedy_server_v2.domain.planner.exception.DuplicateStudyCategoryException;
import com.togedy.togedy_server_v2.domain.planner.exception.StudyCategoryNotFoundException;
import com.togedy.togedy_server_v2.domain.planner.exception.StudyCategoryNotOwnedException;
import com.togedy.togedy_server_v2.domain.schedule.exception.CategoryNotOwnedException;
import com.togedy.togedy_server_v2.domain.user.application.UserService;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyCategoryService {

    private final StudyCategoryRepository studyCategoryRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * 스터디 카테고리를 생성한다.
     *
     * @param request   스터디 카테고리 생성 DTO
     * @param userId    유저 ID
     */
    @Transactional
    public void generateStudyCategory(PostStudyCategoryRequest request, Long userId) {
        validateDuplicateStudyCategory(request.getCategoryName(), request.getCategoryColor(), userId);

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
        StudyCategory studyCategory = studyCategoryRepository.findById(categoryId)
                .orElseThrow(StudyCategoryNotFoundException::new);

        if (!studyCategory.getUserId().equals(userId)) {
            throw new CategoryNotOwnedException();
        }

        validateDuplicateStudyCategory(request.getCategoryName(), request.getCategoryColor(), userId);

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
        StudyCategory studyCategory = studyCategoryRepository.findById(categoryId)
                .orElseThrow(StudyCategoryNotFoundException::new);

        if (!studyCategory.getUserId().equals(userId)) {
            throw new StudyCategoryNotOwnedException();
        }

        studyCategory.delete();
    }

    /**
     * 이름 및 색상이 동일한 스터디 카테고리가 이미 존재하는 지 검증한다.
     *
     * @param categoryName      카테고리명
     * @param categoryColor     카테고리 색상
     * @param userId            유저ID
     */
    private void validateDuplicateStudyCategory(String categoryName, String categoryColor, Long userId) {
        if (studyCategoryRepository.existsByNameAndColorAndUserId(categoryName, categoryColor, userId)) {
            throw new DuplicateStudyCategoryException();
        }
    }

}
