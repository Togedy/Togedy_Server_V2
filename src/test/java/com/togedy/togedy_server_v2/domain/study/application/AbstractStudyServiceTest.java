package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTaskRepository;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.global.service.S3Service;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractStudyServiceTest {

    @Mock
    protected DailyStudySummaryRepository dailyStudySummaryRepository;

    @Mock
    protected StudyRepository studyRepository;

    @Mock
    protected UserStudyRepository userStudyRepository;

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected StudySubjectRepository studySubjectRepository;

    @Mock
    protected StudyTaskRepository studyTaskRepository;

    @Mock
    S3Service s3Service;
}
