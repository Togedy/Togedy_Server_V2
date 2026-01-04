package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.StudyCategoryRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetStudyCategoryResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PostStudyCategoryRequest;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyCategory;
import com.togedy.togedy_server_v2.domain.planner.exception.DuplicateStudyCategoryException;
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

    private void validateDuplicateStudyCategory(String categoryName, String categoryColor, Long userId) {
        if (studyCategoryRepository.existsByNameAndColorAndUserId(categoryName, categoryColor, userId)) {
            throw new DuplicateStudyCategoryException();
        }
    }

}
