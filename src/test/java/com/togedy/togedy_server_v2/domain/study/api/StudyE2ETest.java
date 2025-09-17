package com.togedy.togedy_server_v2.domain.study.api;

import com.togedy.togedy_server_v2.domain.global.support.AuthenticatedE2ETest;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("E2E 테스트")
public class StudyE2ETest extends AuthenticatedE2ETest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    UserStudyRepository userStudyRepository;

    @Test
    @DisplayName("챌린지 스터디를 생성한다.")
    public void createNormalTypeStudy() throws Exception{
        MockMultipartFile image = new MockMultipartFile(
                "studyImage",
                "img.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        var requestBuilder = multipart("/api/v2/studies")
                .file(image)
                .param("studyName", "알고리즘 스터디")
                .param("studyDescription", "백준 2문제 스터디")
                .param("studyMemberLimit", "10")
                .param("studyTag", "자유스터디")
                .param("goalTime", "05:00:00")
                .with(bearer("token"))
                .contentType(MediaType.MULTIPART_FORM_DATA);

        //when
        mockMvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful());

        //then
        Optional<Study> saved = studyRepository.findAll().stream().findFirst();
        assertThat(saved).isPresent();

        Study study = saved.get();

        assertThat(study.getName()).isEqualTo("알고리즘 스터디");
        assertThat(study.getType()).isEqualTo("CHALLENGE");
        assertThat(study.getImageUrl()).isEqualTo("https://mock-s3/test.png");
        assertThat(study.getGoalTime()).isEqualTo(LocalTime.of(5, 0, 0));

        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(study.getId(), 1L)
                .orElseThrow();

        assertThat(userStudy.getRole()).isEqualTo(StudyRole.LEADER.name());
    }
}
