package com.togedy.togedy_server_v2.domain.planner.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTaskRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.DailyPlannerTaskDto;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerTaskResponse;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTask;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StudyTaskServiceTest {

    @Mock
    private StudyTaskRepository studyTaskRepository;

    @Mock
    private StudySubjectRepository studySubjectRepository;

    @Mock
    private StudyTimeRepository studyTimeRepository;

    @InjectMocks
    private StudyTaskService studyTaskService;

    @Test
    void 일간_플래너_테스크_조회_시_과목별_테스크와_공부시간을_반환한다() {
        Long userId = 1L;
        LocalDate date = LocalDate.of(2026, 2, 17);

        StudySubject english = StudySubject.builder()
                .userId(userId)
                .name("영어")
                .color("노란색")
                .orderIndex(1L)
                .build();
        ReflectionTestUtils.setField(english, "id", 1L);

        StudySubject math = StudySubject.builder()
                .userId(userId)
                .name("수학")
                .color("파란색")
                .orderIndex(2L)
                .build();
        ReflectionTestUtils.setField(math, "id", 2L);

        StudyTask task1 = StudyTask.builder()
                .userId(userId)
                .studySubjectId(1L)
                .name("영어단어 10개 암기")
                .date(date)
                .build();
        ReflectionTestUtils.setField(task1, "id", 101L);
        task1.setChecked(true);

        StudyTask task2 = StudyTask.builder()
                .userId(userId)
                .studySubjectId(1L)
                .name("문법 공부 30분")
                .date(date)
                .build();
        ReflectionTestUtils.setField(task2, "id", 102L);
        task2.setChecked(true);

        StudyTime time1 = org.mockito.Mockito.mock(StudyTime.class);
        StudyTime time2 = org.mockito.Mockito.mock(StudyTime.class);
        given(time1.getStudySubjectId()).willReturn(1L);
        given(time1.getStartTime()).willReturn(LocalDateTime.of(2026, 2, 17, 9, 0, 0));
        given(time1.getEndTime()).willReturn(LocalDateTime.of(2026, 2, 17, 9, 10, 0));
        given(time2.getStudySubjectId()).willReturn(1L);
        given(time2.getStartTime()).willReturn(LocalDateTime.of(2026, 2, 17, 10, 0, 0));
        given(time2.getEndTime()).willReturn(LocalDateTime.of(2026, 2, 17, 10, 2, 0));

        given(studySubjectRepository.findAllByUserId(userId))
                .willReturn(List.of(english, math));
        given(studyTaskRepository.findAllByStudySubjectIdsAndDate(List.of(1L, 2L), date))
                .willReturn(List.of(task1, task2));
        given(studyTimeRepository.findDailyStudyTimesBySubjectIds(
                List.of(1L, 2L),
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        )).willReturn(List.of(time1, time2));

        GetDailyPlannerTaskResponse response = studyTaskService.findDailyPlannerTasks(date, userId);

        assertThat(response.getDailyPlanner()).hasSize(2);

        DailyPlannerTaskDto englishPlanner = response.getDailyPlanner().get(0);
        assertThat(englishPlanner.getSubjectId()).isEqualTo(1L);
        assertThat(englishPlanner.getSubjectName()).isEqualTo("영어");
        assertThat(englishPlanner.getSubjectColor()).isEqualTo("노란색");
        assertThat(englishPlanner.getSubjectStudyTime()).isEqualTo(720L);
        assertThat(englishPlanner.getTaskList()).hasSize(2);
        assertThat(englishPlanner.getTaskList().get(0).getTaskId()).isEqualTo(101L);
        assertThat(englishPlanner.getTaskList().get(0).getTaskName()).isEqualTo("영어단어 10개 암기");
        assertThat(englishPlanner.getTaskList().get(0).isChecked()).isTrue();

        DailyPlannerTaskDto mathPlanner = response.getDailyPlanner().get(1);
        assertThat(mathPlanner.getSubjectId()).isEqualTo(2L);
        assertThat(mathPlanner.getSubjectName()).isEqualTo("수학");
        assertThat(mathPlanner.getSubjectColor()).isEqualTo("파란색");
        assertThat(mathPlanner.getSubjectStudyTime()).isEqualTo(0L);
        assertThat(mathPlanner.getTaskList()).isEmpty();
    }
}
