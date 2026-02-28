package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartResponse;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.domain.planner.exception.StudySubjectNotFoundException;
import com.togedy.togedy_server_v2.domain.planner.exception.StudySubjectNotOwnedException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerAlreadyRunningException;
import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimerService {

    private final StudySubjectRepository studySubjectRepository;
    private final StudyTimeRepository studyTimeRepository;

    @Transactional
    public PostTimerStartResponse startTimer(PostTimerStartRequest request, Long userId) {
        validateStartRequest(request);

        if (studyTimeRepository.findByUserIdAndEndTimeIsNull(userId).isPresent()) {
            throw new TimerAlreadyRunningException();
        }

        StudySubject studySubject = studySubjectRepository.findActiveById(request.getStudySubjectId())
                .orElseThrow(StudySubjectNotFoundException::new);

        if (!studySubject.getUserId().equals(userId)) {
            throw new StudySubjectNotOwnedException();
        }

        LocalDateTime startTime = LocalDateTime.now();

        StudyTime studyTime = StudyTime.builder()
                .userId(userId)
                .studySubjectId(studySubject.getId())
                .startTime(startTime)
                .endTime(null)
                .build();

        Long timerId = studyTimeRepository.save(studyTime).getId();
        return PostTimerStartResponse.of(timerId, startTime);
    }

    private void validateStartRequest(PostTimerStartRequest request) {
        if (request == null || request.getStudySubjectId() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "studySubjectId는 필수입니다.");
        }
    }
}
