package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetRunningTimerResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.GetTimerTotalResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStopRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStopResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.SubjectStudyTimeItemResponse;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.domain.planner.exception.InvalidStudySubjectException;
import com.togedy.togedy_server_v2.domain.planner.exception.StudySubjectNotFoundException;
import com.togedy.togedy_server_v2.domain.planner.exception.StudySubjectNotOwnedException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerAlreadyRunningException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerAlreadyStoppedException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerNotFoundException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerNotOwnedException;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.enums.UserStatus;
import com.togedy.togedy_server_v2.domain.user.exception.user.UserNotFoundException;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimerService {

    private final UserRepository userRepository;
    private final DailyStudySummaryRepository dailyStudySummaryRepository;
    private final StudySubjectRepository studySubjectRepository;
    private final StudyTimeRepository studyTimeRepository;

    @Transactional
    public PostTimerStartResponse startTimer(PostTimerStartRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

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
                .isRunning(true)
                .build();

        Long timerId;
        try {
            timerId = studyTimeRepository.saveAndFlush(studyTime).getId();
        } catch (DataIntegrityViolationException e) {
            throw new TimerAlreadyRunningException();
        }

        user.updateStatus(UserStatus.STUDYING);

        return PostTimerStartResponse.of(timerId, startTime);
    }

    @Transactional
    public PostTimerStopResponse stopTimer(PostTimerStopRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        validateStopRequest(request);

        StudyTime studyTime = studyTimeRepository.findByIdForUpdate(request.getTimerId())
                .orElseThrow(TimerNotFoundException::new);

        if (!studyTime.getUserId().equals(userId)) {
            throw new TimerNotOwnedException();
        }
        if (studyTime.getEndTime() != null) {
            throw new TimerAlreadyStoppedException();
        }

        LocalDateTime endTime = LocalDateTime.now();
        studyTime.stop(endTime);
        updateDailyStudySummary(userId, studyTime.getStartTime(), endTime);
        user.updateStatus(UserStatus.ACTIVE);
        return PostTimerStopResponse.of(studyTime.getId(), studyTime.getStartTime(), endTime);
    }

    @Transactional(readOnly = true)
    public GetRunningTimerResponse findRunningTimer(Long userId) {
        return studyTimeRepository.findByUserIdAndEndTimeIsNull(userId)
                .map(studyTime -> GetRunningTimerResponse.of(
                        studyTime.getId(),
                        studyTime.getStudySubjectId(),
                        studyTime.getStartTime()
                ))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<SubjectStudyTimeItemResponse> findTodaySubjectStudyTimes(Long userId) {
        List<StudySubject> studySubjects = studySubjectRepository.findAllByUserId(userId);
        if (studySubjects.isEmpty()) {
            return List.of();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = TimeUtil.startOfStudyDay(now);
        LocalDateTime dayEnd = TimeUtil.endOfStudyDay(now);

        Map<Long, Long> studyTimeBySubjectId = new HashMap<>();
        List<Object[]> rows = studyTimeRepository.findDailyStudyTimeBySubject(userId, dayStart, dayEnd);
        for (Object[] row : rows) {
            Long subjectId = ((Number) row[0]).longValue();
            Long studyTime = ((Number) row[1]).longValue();
            studyTimeBySubjectId.put(subjectId, studyTime);
        }

        List<SubjectStudyTimeItemResponse> response = studySubjects.stream()
                .map(subject -> SubjectStudyTimeItemResponse.of(
                        subject.getId(),
                        subject.getName(),
                        studyTimeBySubjectId.getOrDefault(subject.getId(), 0L)
                ))
                .toList();

        return response;
    }

    @Transactional(readOnly = true)
    public GetTimerTotalResponse findTodayTotalStudyTime(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = TimeUtil.startOfStudyDay(now);
        LocalDateTime dayEnd = TimeUtil.endOfStudyDay(now);

        long totalStudyTime = studyTimeRepository.sumDailyStudyTimeByUserId(userId, dayStart, dayEnd);

        return GetTimerTotalResponse.of(totalStudyTime);
    }

    private void validateStartRequest(PostTimerStartRequest request) {
        if (request == null || request.getStudySubjectId() == null || request.getStudySubjectId() <= 0) {
            throw new InvalidStudySubjectException();
        }
    }

    private void validateStopRequest(PostTimerStopRequest request) {
        if (request == null || request.getTimerId() == null || request.getTimerId() <= 0) {
            throw new TimerNotFoundException();
        }
    }

    private void updateDailyStudySummary(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            return;
        }

        Map<LocalDate, Long> additionalStudyTimeByDate = splitByStudyDay(startTime, endTime);
        additionalStudyTimeByDate.forEach(
                (date, additionalStudyTime) -> upsertDailyStudySummary(userId, date, additionalStudyTime));
    }

    private Map<LocalDate, Long> splitByStudyDay(LocalDateTime startTime, LocalDateTime endTime) {
        Map<LocalDate, Long> additionalStudyTimeByDate = new HashMap<>();
        LocalDateTime cursor = startTime;

        while (cursor.isBefore(endTime)) {
            LocalDateTime nextBoundary = TimeUtil.endOfStudyDay(cursor);
            LocalDateTime chunkEnd = nextBoundary.isBefore(endTime) ? nextBoundary : endTime;

            long seconds = Duration.between(cursor, chunkEnd).getSeconds();
            if (seconds > 0) {
                LocalDate studyDate = TimeUtil.startOfStudyDay(cursor).toLocalDate();
                additionalStudyTimeByDate.merge(studyDate, seconds, Long::sum);
            }
            cursor = chunkEnd;
        }

        return additionalStudyTimeByDate;
    }

    private void upsertDailyStudySummary(Long userId, LocalDate date, Long additionalStudyTime) {
        DailyStudySummary dailyStudySummary = dailyStudySummaryRepository.findByUserIdAndDateForUpdate(userId, date)
                .orElseGet(() -> DailyStudySummary.builder()
                        .userId(userId)
                        .studyTime(0L)
                        .date(date)
                        .build());

        dailyStudySummary.addStudyTime(additionalStudyTime);
        try {
            dailyStudySummaryRepository.save(dailyStudySummary);
        } catch (DataIntegrityViolationException e) {
            DailyStudySummary existingSummary = dailyStudySummaryRepository.findByUserIdAndDateForUpdate(userId, date)
                    .orElseThrow(() -> e);
            existingSummary.addStudyTime(additionalStudyTime);
            dailyStudySummaryRepository.save(existingSummary);
        }
    }

}
