package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetStudySubjectResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PatchReorderRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PatchStudySubjectRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostStudySubjectRequest;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.exception.DuplicateStudySubjectException;
import com.togedy.togedy_server_v2.domain.planner.exception.InvalidStudySubjectReorderException;
import com.togedy.togedy_server_v2.domain.planner.exception.StudySubjectNotFoundException;
import com.togedy.togedy_server_v2.domain.planner.exception.StudySubjectNotOwnedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudySubjectService {

    private final StudySubjectRepository studySubjectRepository;

    private static final long ORDER_GAP = 1000L;

    /**
     * 스터디 과목을 생성한다.
     *
     * @param request   스터디 과목 생성 DTO
     * @param userId    유저 ID
     */
    @Transactional
    public void generateStudySubject(PostStudySubjectRequest request, Long userId) {
        validateDuplicateOnCreate(request.getSubjectName(), request.getSubjectColor(), userId);

        Long lastOrder = studySubjectRepository.findMaxOrderIndex(userId);
        Long nextOrder = lastOrder == null ? 1000L : lastOrder + 1000L;

        StudySubject studySubject = StudySubject.builder()
                .userId(userId)
                .name(request.getSubjectName())
                .color(request.getSubjectColor())
                .orderIndex(nextOrder)
                .build();

        studySubjectRepository.save(studySubject);
    }

    /**
     * 유저가 보유하고 있는 모든 스터디 과목을 조회한다.
     *
     * @param userId    유저ID
     * @return          유저가 보유한 스터디 과목 정보 DTO List
     */
    @Transactional(readOnly = true)
    public List<GetStudySubjectResponse> findAllStudySubjectsByUserId(Long userId) {
        List<StudySubject> studySubjectList = studySubjectRepository.findAllByUserId(userId);

        return studySubjectList.stream()
                .map(GetStudySubjectResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 유저가 해당 스터디 과목의 정보를 수정한다.
     *
     * @param request       과목 수정 DTO
     * @param subjectId    수정할 과목ID
     * @param userId        유저ID
     */
    @Transactional
    public void modifyStudySubject(PatchStudySubjectRequest request, Long subjectId, Long userId) {
        StudySubject studySubject = findOwnedSubject(subjectId, userId);

        validateDuplicateOnUpdate(request.getSubjectName(), request.getSubjectColor(), userId, subjectId);

        studySubject.update(request);
    }

    /**
     * 유저가 해당 스터디 과목을 삭제한다.
     *
     * @param subjectId    삭제할 과목ID
     * @param userId        유저ID
     */
    @Transactional
    public void removeStudySubject(Long subjectId, Long userId) {
        StudySubject studySubject = findOwnedSubject(subjectId, userId);

        studySubject.delete();
    }

    /**
     * 유저가 해당 스터디 과목의 정렬 순서를 변경한다.
     *
     * @param request       정렬 수정 요청 DTO
     * @param subjectId    정렬 대상 과목ID
     * @param userId        유저ID
     */
    @Transactional
    public void reorderStudySubject(PatchReorderRequest request, Long subjectId, Long userId) {
        validateStudySubjectReorder(request, subjectId);

        StudySubject target = findOwnedSubject(subjectId, userId);
        StudySubject prev = getPrevSubject(request, userId);
        StudySubject next = getNextSubject(request, userId);

        long newOrderIndex = calculateNewOrderIndex(prev, next);
        target.move(newOrderIndex);
    }

    /**
     * 생성 시에 이름 및 색상이 동일한 스터디 과목이 이미 존재하는 지 검증한다.
     *
     * @param subjectName      과목명
     * @param subjectColor     과목 색상
     * @param userId            유저ID
     */
    private void validateDuplicateOnCreate(String subjectName, String subjectColor, Long userId) {
        if (studySubjectRepository.existsByNameAndColorAndUserId(subjectName, subjectColor, userId)) {
            throw new DuplicateStudySubjectException();
        }
    }

    /**
     * 수정 시에 이름 및 색상이 동일한 스터디 과목이 이미 존재하는 지 검증한다.
     *
     * @param subjectName      과목명
     * @param subjectColor     과목색상
     * @param userId           유저ID
     * @param subjectId        비교 과목ID
     */
    private void validateDuplicateOnUpdate(String subjectName, String subjectColor, Long userId, Long subjectId) {
        studySubjectRepository
                .findByNameAndColorAndUserId(subjectName, subjectColor, userId)
                .filter(category -> !category.getId().equals(subjectId))
                .ifPresent(c -> {
                    throw new DuplicateStudySubjectException();
                });
    }

    /**
     * 과목 정렬 수정의 유효성을 검증한다.
     *
     * @param request       정렬 수정 요청 DTO
     * @param subjectId    정렬 대상 과목ID
     */
    private void validateStudySubjectReorder(PatchReorderRequest request, Long subjectId) {
        if(request.getPrevId() == null && request.getNextId() == null) {
            throw new InvalidStudySubjectReorderException();
        }

        if (request.getPrevId() != null && request.getPrevId().equals(subjectId)) {
            throw new InvalidStudySubjectReorderException();
        }
        if (request.getNextId() != null && request.getNextId().equals(subjectId)) {
            throw new InvalidStudySubjectReorderException();
        }
    }

    private long calculateNewOrderIndex(StudySubject prev, StudySubject next) {
        if (prev == null) {
            return next.getOrderIndex() - ORDER_GAP;
        }

        if (next == null) {
            return prev.getOrderIndex() + ORDER_GAP;
        }

        long gap = next.getOrderIndex() - prev.getOrderIndex();
        if (gap <= 1) {
            throw new InvalidStudySubjectReorderException();
        }

        return prev.getOrderIndex() + gap / 2;
    }

    private StudySubject findOwnedSubject(Long subjectId, Long userId) {
        StudySubject subject = studySubjectRepository.findActiveById(subjectId)
                .orElseThrow(StudySubjectNotFoundException::new);

        if (!subject.getUserId().equals(userId)) {
            throw new StudySubjectNotOwnedException();
        }
        return subject;
    }

    private StudySubject getPrevSubject(PatchReorderRequest request, Long userId) {
        return request.getPrevId() == null
                ? null
                : findOwnedSubject(request.getPrevId(), userId);
    }

    private StudySubject getNextSubject(PatchReorderRequest request, Long userId) {
        return request.getNextId() == null
                ? null
                : findOwnedSubject(request.getNextId(), userId);
    }

}
