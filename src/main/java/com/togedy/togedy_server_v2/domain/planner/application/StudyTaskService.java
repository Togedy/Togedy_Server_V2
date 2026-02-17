package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTaskRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.DailyPlannerTaskDto;
import com.togedy.togedy_server_v2.domain.planner.dto.DailyPlannerTaskItemDto;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerTaskResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PutStudyTaskRequest;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTask;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.domain.planner.exception.*;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        List<StudySubject> studySubjects = studySubjectRepository.findAllByUserId(userId);
        List<Long> studySubjectIds = studySubjects.stream()
                .map(StudySubject::getId)
                .toList();

        if (studySubjectIds.isEmpty()) {
            return GetDailyPlannerTaskResponse.of(List.of());
        }

        List<StudyTask> dailyStudyTasks = studyTaskRepository.findAllByStudySubjectIdsAndDate(studySubjectIds, date);
        Map<Long, List<StudyTask>> tasksByStudySubjectId = dailyStudyTasks.stream()
                .collect(Collectors.groupingBy(StudyTask::getStudySubjectId));

        LocalDateTime startOfDate = date.atStartOfDay();
        LocalDateTime startOfNextDate = date.plusDays(1).atStartOfDay();

        List<StudyTime> studyTimes = studyTimeRepository.findDailyStudyTimesBySubjectIds(
                studySubjectIds, startOfDate, startOfNextDate
        );

        Map<Long, Long> studyTimeBySubjectId = studyTimes.stream()
                .collect(Collectors.groupingBy(
                        StudyTime::getStudySubjectId,
                        Collectors.summingLong(studyTime ->
                                Math.max(0L, Duration.between(studyTime.getStartTime(), studyTime.getEndTime()).getSeconds()))
                ));

        List<DailyPlannerTaskDto> dailyPlanner = new ArrayList<>();
        for (StudySubject studySubject : studySubjects) {
            List<DailyPlannerTaskItemDto> taskList = tasksByStudySubjectId
                    .getOrDefault(studySubject.getId(), List.of())
                    .stream()
                    .map(DailyPlannerTaskItemDto::from)
                    .toList();

            String subjectStudyTime = TimeUtil.formatSecondsToHms(
                    studyTimeBySubjectId.getOrDefault(studySubject.getId(), 0L)
            );

            dailyPlanner.add(DailyPlannerTaskDto.of(studySubject, subjectStudyTime, taskList));
        }

        return GetDailyPlannerTaskResponse.of(dailyPlanner);
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
                    .date(request.getDate())
                    .build();
            return studyTaskRepository.save(task).getId();
        }

        StudyTask task = validateTask(request.getTaskId(), userId);
        task.update(request.getName());
        return task.getId();
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
}
