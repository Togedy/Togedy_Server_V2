package com.togedy.togedy_server_v2.domain.study.api;

import com.togedy.togedy_server_v2.domain.global.fixtures.StudyFixture;
import com.togedy.togedy_server_v2.domain.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.domain.global.support.AuthenticatedE2ETest;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyMemberLimitRequest;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.study.enums.StudyTag;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("E2E 테스트")
public class StudyE2ETest extends AuthenticatedE2ETest {

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    UserStudyRepository userStudyRepository;

    @Test
    @DisplayName("챌린지 스터디를 생성한다.")
    public void createChallengeStudy() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "studyImage",
                "img.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        MockHttpServletRequestBuilder requestBuilder = multipart("/api/v2/studies")
                .file(image)
                .param("studyName", "챌린지 스터디")
                .param("studyDescription", "챌린지 스터디를 생성한다.")
                .param("studyMemberLimit", "30")
                .param("studyTag", "내신/학교 생활")
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

        assertThat(study.getName()).isEqualTo("챌린지 스터디");
        assertThat(study.getType()).isEqualTo("CHALLENGE");
        assertThat(study.getDescription()).isEqualTo("챌린지 스터디를 생성한다.");
        assertThat(study.getMemberLimit()).isEqualTo(30);
        assertThat(study.getImageUrl()).isEqualTo("https://mock-s3/test.png");
        assertThat(study.getGoalTime()).isEqualTo(LocalTime.of(5, 0, 0));

        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(study.getId(), 1L)
                .orElseThrow();

        assertThat(userStudy.getRole()).isEqualTo(StudyRole.LEADER.name());
    }

    @Test
    @DisplayName("일반 스터디를 생성한다.")
    public void createNormalStudy() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "studyImage",
                "img.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        MockHttpServletRequestBuilder requestBuilder = multipart("/api/v2/studies")
                .file(image)
                .param("studyName", "일반 스터디")
                .param("studyDescription", "일반 스터디를 생성한다.")
                .param("studyMemberLimit", "10")
                .param("studyTag", "내신/학교생활")
                .with(bearer("token"))
                .contentType(MediaType.MULTIPART_FORM_DATA);

        //when
        mockMvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful());

        //then
        Optional<Study> saved = studyRepository.findAll().stream().findFirst();
        assertThat(saved).isPresent();

        Study study = saved.get();

        assertThat(study.getName()).isEqualTo("일반 스터디");
        assertThat(study.getType()).isEqualTo("NORMAL");
        assertThat(study.getDescription()).isEqualTo("일반 스터디를 생성한다.");
        assertThat(study.getMemberLimit()).isEqualTo(10);
        assertThat(study.getImageUrl()).isEqualTo("https://mock-s3/test.png");

        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(study.getId(), 1L)
                .orElseThrow();

        assertThat(userStudy.getRole()).isEqualTo(StudyRole.LEADER.name());
    }

    @DisplayName("리더가 스터디를 조회한다.")
    @Test
    public void findStudyByLeader() throws Exception {
        //given
        User user = UserFixture.createUser();
        Study study = StudyFixture.createChallengeStudy();

        fixtureSupport.persistUser(user);
        fixtureSupport.persistStudy(study);
        fixtureSupport.persistUserStudy(study, user, StudyRole.LEADER);

        //when
        MockHttpServletRequestBuilder requestBuilder =
                get("/api/v2/studies/{studyId}", study.getId())
                        .header("Authorization", "Bearer token");

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response.isStudyLeader").value(true))
                .andExpect(jsonPath("$.response.studyName").value(study.getName()))
                .andExpect(jsonPath("$.response.studyDescription").value(study.getDescription()))
                .andExpect(jsonPath("$.response.studyImageUrl").value(study.getImageUrl()))
                .andExpect(jsonPath("$.response.studyTag").value(study.getTag()))
                .andExpect(jsonPath("$.response.studyTier").value(study.getTier()))
                .andExpect(jsonPath("$.response.studyMemberCount").value(1))
//                .andExpect(jsonPath("$.response.completedMemberCount").value(null))
                .andExpect(jsonPath("$.response.studyMemberLimit").value(study.getMemberLimit()))
                .andExpect(jsonPath("$.response.studyPassword").value(Matchers.nullValue()));
    }

    @DisplayName("리더가 스터디 정보를 수정한다.")
    @Test
    public void modifyStudyInfo() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "studyImage",
                "img.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        Study study = StudyFixture.createNormalStudy();
        User user = UserFixture.createUser();

        fixtureSupport.persistStudy(study);
        fixtureSupport.persistUser(user);
        fixtureSupport.persistUserStudy(study, user, StudyRole.LEADER);

        //when
        MockHttpServletRequestBuilder requestBuilder =
                multipart("/api/v2/studies/{studyId}/information", study.getId())
                        .file(image)
                        .param("studyName", "스터디 이름 변경")
                        .param("studyDescription", "스터디 이름을 변경한다.")
                        .param("studyTag", StudyTag.FREE.getDescription())
                        .param("studyPassword", "1234")
                        .header("Authorization", "Bearer token")
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        });

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful());

        Optional<Study> saved = studyRepository.findById(study.getId());
        assertThat(saved).isPresent();

        Study savedStudy = saved.get();

        assertThat(savedStudy.getName()).isEqualTo("스터디 이름 변경");
        assertThat(savedStudy.getDescription()).isEqualTo("스터디 이름을 변경한다.");
        assertThat(savedStudy.getTag()).isEqualTo(StudyTag.FREE.getDescription());
        assertThat(savedStudy.getImageUrl()).isEqualTo("https://mock-s3/test.png");
        assertThat(savedStudy.getPassword()).isEqualTo("1234");
    }

    @DisplayName("리더가 스터디 최대 인원을 수정한다.")
    @Test
    public void modifyStudyMemberLimit() throws Exception {
        //given
        Study study = StudyFixture.createNormalStudy();
        User user = UserFixture.createUser();

        fixtureSupport.persistStudy(study);
        fixtureSupport.persistUser(user);
        fixtureSupport.persistUserStudy(study, user, StudyRole.LEADER);

        PatchStudyMemberLimitRequest body = new PatchStudyMemberLimitRequest(20);

        //when
        MockHttpServletRequestBuilder requestBuilder =
                patch("/api/v2/studies/{studyId}/members/limit", study.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("Authorization", "Bearer token");

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful());

        Optional<Study> saved = studyRepository.findById(study.getId());
        assertThat(saved).isPresent();

        Study savedStudy = saved.get();

        assertThat(savedStudy.getMemberLimit()).isEqualTo(20);
    }
}
