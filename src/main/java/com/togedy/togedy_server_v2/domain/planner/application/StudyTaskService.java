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
        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidStudyTaskNameException();
        }

        StudySubject subject = studySubjectRepository.findById(request.getStudySubjectId())
                .orElseThrow(StudySubjectNotFoundException::new);

        if (!subject.getUserId().equals(userId)) {
            throw new StudySubjectNotOwnedException();
        }

        if (request.getTaskId() == null) {
            StudyTask task = StudyTask.builder()
                    .userId(userId)
                    .studySubjectId(subject.getId())
                    .name(request.getName())
                    .date(request.getDate())
                    .build();
            return studyTaskRepository.save(task).getId();
        }

        StudyTask task = studyTaskRepository.findById(request.getTaskId())
                .orElseThrow(StudyTaskNotFoundException::new);

        if (!task.getUserId().equals(userId)) {
            throw new StudyTaskNotOwnedException();
        }

        task.update(request.getName());
        return task.getId();
    }

    @Transactional
    public void deleteStudyTask(Long taskId, Long userId) {

        StudyTask task = studyTaskRepository.findById(taskId)
                .orElseThrow(StudyTaskNotFoundException::new);

        if (!task.getUserId().equals(userId)) {
            throw new StudyTaskNotOwnedException();
        }

        studyTaskRepository.delete(task);
    }
}
