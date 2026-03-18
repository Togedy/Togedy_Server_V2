package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTaskRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.DailyPlannerTaskDto;
import com.togedy.togedy_server_v2.domain.planner.dto.DailyPlannerTaskItemDto;
import com.togedy.togedy_server_v2.domain.planner.dto.DailyPlannerShareItemResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.DailyPlannerShareTaskItemResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerTaskResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PutStudyTaskRequest;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTask;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.domain.planner.exception.*;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class StudyTaskService {

    private final StudyTaskRepository studyTaskRepository;
    private final StudySubjectRepository studySubjectRepository;
    private final StudyTimeRepository studyTimeRepository;

    @Transactional(readOnly = true)
    public GetDailyPlannerTaskResponse findDailyPlannerTasks(LocalDate date, Long userId) {
        LocalDate studyDate = resolveStudyDate(date);
        return GetDailyPlannerTaskResponse.of(buildDailyPlannerTasks(studyDate, userId));
    }

    @Transactional(readOnly = true)
    public List<DailyPlannerShareItemResponse> findDailyPlannerShareItems(LocalDate date, Long userId) {
        LocalDate studyDate = resolveStudyDate(date);

        return buildDailyPlannerTasks(studyDate, userId).stream()
                .map(item -> DailyPlannerShareItemResponse.of(
                        item.getSubjectId(),
                        item.getSubjectName(),
                        item.getSubjectColor(),
                        item.getTaskList().size(),
                        (int) item.getTaskList().stream().filter(DailyPlannerTaskItemDto::isChecked).count(),
                        item.getTaskList().stream()
                                .map(task -> DailyPlannerShareTaskItemResponse.of(task.getTaskName(), task.isChecked()))
                                .toList()
                ))
                .toList();
    }

    @Transactional
    public Long upsertStudyTask(PutStudyTaskRequest request, Long userId) {
        validateTaskName(request.getName());

        StudySubject subject = validateSubject(request.getStudySubjectId(), userId);

        if (request.getTaskId() == null) {
            StudyTask task = StudyTask.builder()
                    .userId(userId)
                    .studySubjectId(subject.getId())
                    .name(request.getName())
                    .date(resolveStudyDate(request.getDate()))
                    .build();
            return studyTaskRepository.save(task).getId();
        }

        StudyTask task = validateTask(request.getTaskId(), userId);
        task.update(request.getName());
        return task.getId();
    }

    private List<DailyPlannerTaskDto> buildDailyPlannerTasks(LocalDate studyDate, Long userId) {
        List<StudySubject> studySubjects = studySubjectRepository.findAllByUserId(userId);
        List<Long> studySubjectIds = studySubjects.stream()
                .map(StudySubject::getId)
                .toList();

        if (studySubjectIds.isEmpty()) {
            return List.of();
        }

        List<StudyTask> dailyStudyTasks = studyTaskRepository.findAllByStudySubjectIdsAndDate(studySubjectIds, studyDate);
        Map<Long, List<StudyTask>> tasksByStudySubjectId = dailyStudyTasks.stream()
                .collect(Collectors.groupingBy(StudyTask::getStudySubjectId));

        LocalDateTime startOfDate = studyDate.atTime(5, 0);
        LocalDateTime startOfNextDate = startOfDate.plusDays(1);

        List<StudyTime> studyTimes = studyTimeRepository.findDailyStudyTimesBySubjectIds(
                studySubjectIds, startOfDate, startOfNextDate
        );

        Map<Long, Long> studyTimeBySubjectId = studyTimes.stream()
                .collect(Collectors.groupingBy(
                        StudyTime::getStudySubjectId,
                        Collectors.summingLong(studyTime ->
                                calculateOverlapStudySeconds(studyTime, startOfDate, startOfNextDate))
                ));

        return studySubjects.stream()
                .map(studySubject -> {
                    List<DailyPlannerTaskItemDto> taskList = tasksByStudySubjectId
                            .getOrDefault(studySubject.getId(), List.of())
                            .stream()
                            .map(DailyPlannerTaskItemDto::from)
                            .toList();

                    Long subjectStudyTime = studyTimeBySubjectId.getOrDefault(studySubject.getId(), 0L);

                    return DailyPlannerTaskDto.of(studySubject, subjectStudyTime, taskList);
                })
                .toList();
    }

    @Transactional
    public void deleteStudyTask(Long taskId, Long userId) {
        StudyTask task = validateTask(taskId, userId);
        studyTaskRepository.delete(task);
    }

    @Transactional
    public void checkStudyTask(Long taskId, boolean isChecked, Long userId) {
        StudyTask task = validateTask(taskId, userId);
        task.setChecked(isChecked);
    }

    private StudyTask validateTask(Long taskId, Long userId) {
        StudyTask task = studyTaskRepository.findById(taskId)
                .orElseThrow(StudyTaskNotFoundException::new);

        if (!task.getUserId().equals(userId)) {
            throw new StudyTaskNotOwnedException();
        }
        return task;
    }

    private StudySubject validateSubject(Long subjectId, Long userId) {
        StudySubject subject = studySubjectRepository.findById(subjectId)
                .orElseThrow(StudySubjectNotFoundException::new);

        if (!subject.getUserId().equals(userId)) {
            throw new StudySubjectNotOwnedException();
        }
        return subject;
    }

    private void validateTaskName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidStudyTaskNameException();
        }
    }

    private long calculateOverlapStudySeconds(StudyTime studyTime, LocalDateTime periodStart, LocalDateTime periodEnd) {
        LocalDateTime effectiveStart = studyTime.getStartTime().isAfter(periodStart) ? studyTime.getStartTime() : periodStart;
        LocalDateTime effectiveEnd = studyTime.getEndTime().isBefore(periodEnd) ? studyTime.getEndTime() : periodEnd;
        return TimeUtil.calculateStudySeconds(effectiveStart, effectiveEnd);
    }

    private LocalDate resolveStudyDate(LocalDate requestedDate) {
        if (requestedDate != null && requestedDate.equals(LocalDate.now())) {
            return TimeUtil.currentStudyDate();
        }
        return requestedDate;
    }
}
