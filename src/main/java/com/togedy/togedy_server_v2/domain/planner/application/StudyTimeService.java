package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.DailyTimetableItemResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyTimetableResponse;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyTimeService {

    private final StudySubjectRepository studySubjectRepository;
    private final StudyTimeRepository studyTimeRepository;

    @Transactional(readOnly = true)
    public GetDailyTimetableResponse findDailyTimetables(LocalDate date, Long userId) {
        List<StudySubject> studySubjects = studySubjectRepository.findAllByUserId(userId);
        if (studySubjects.isEmpty()) {
            return GetDailyTimetableResponse.of(List.of());
        }

        List<Long> studySubjectIds = studySubjects.stream()
                .map(StudySubject::getId)
                .toList();

        LocalDateTime dayStart = date.atTime(5, 0);
        LocalDateTime dayEnd = dayStart.plusDays(1);

        List<StudyTime> studyTimes = studyTimeRepository.findDailyStudyTimesBySubjectIds(
                studySubjectIds,
                dayStart,
                dayEnd
        );

        Map<Long, String> subjectColorById = studySubjects.stream()
                .collect(Collectors.toMap(StudySubject::getId, StudySubject::getColor));

        List<DailyTimetableItemResponse> timeTableList = studyTimes.stream()
                .sorted(Comparator.comparing(StudyTime::getStartTime))
                .map(studyTime -> DailyTimetableItemResponse.of(
                        studyTime,
                        subjectColorById.get(studyTime.getStudySubjectId())
                ))
                .toList();

        return GetDailyTimetableResponse.of(timeTableList);
    }
}
