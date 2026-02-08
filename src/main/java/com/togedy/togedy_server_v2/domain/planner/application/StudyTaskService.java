package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTaskRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.PutStudyTaskRequest;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTask;
import com.togedy.togedy_server_v2.domain.planner.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class StudyTaskService {

    private final StudyTaskRepository studyTaskRepository;
    private final StudySubjectRepository studySubjectRepository;

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
        task.toggleChecked(isChecked);
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
