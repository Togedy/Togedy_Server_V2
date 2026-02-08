package com.togedy.togedy_server_v2.domain.study.entity;

import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.enums.StudyTag;
import com.togedy.togedy_server_v2.domain.study.enums.StudyType;
import com.togedy.togedy_server_v2.domain.study.exception.InvalidStudyMemberLimitException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMemberCountExceededException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMemberLimitOutOfRangeException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMinimumMemberRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyPasswordMismatchException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyPasswordRequiredException;
import com.togedy.togedy_server_v2.global.fixtures.DailyStudySummaryFixture;
import com.togedy.togedy_server_v2.global.fixtures.StudyFixture;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class StudyTest {

    @Test
    public void 스터디를_생성_시_멤버_수는_1로_시작한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        // when & then
        Assertions.assertThat(study.getMemberCount()).isEqualTo(1);
    }

    @Test
    public void 스터디_이름을_변경한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        // when
        study.updateInformation("수정", null, null, null, null);

        // then
        Assertions.assertThat(study.getName()).isEqualTo("수정");
    }

    @Test
    public void 스터디_설명을_변경한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        // when
        study.updateInformation(null, "수정", null, null, null);

        // then
        Assertions.assertThat(study.getDescription()).isEqualTo("수정");
    }

    @Test
    public void 스터디_태그를_변경한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        // when
        study.updateInformation(null, null, StudyTag.FREE.getDescription(), null, null);

        // then
        Assertions.assertThat(study.getTag()).isEqualTo(StudyTag.FREE);
    }

    @Test
    public void 스터디_비밀번호를_변경한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        // when
        study.updateInformation(null, null, null, "수정", null);

        // then
        Assertions.assertThat(study.getPassword()).isEqualTo("수정");
    }

    @Test
    public void 스터디_이미지_URL을_변경한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        // when
        study.updateInformation(null, null, null, null, "수정");

        // then
        Assertions.assertThat(study.getImageUrl()).isEqualTo("수정");
    }

    @Test
    public void 스터디_정보를_모두_변경한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        // when
        study.updateInformation("수정", "수정", StudyTag.FREE.getDescription(), "수정", "수정");

        // then
        Assertions.assertThat(study.getName()).isEqualTo("수정");
        Assertions.assertThat(study.getDescription()).isEqualTo("수정");
        Assertions.assertThat(study.getTag()).isEqualTo(StudyTag.FREE);
        Assertions.assertThat(study.getPassword()).isEqualTo("수정");
        Assertions.assertThat(study.getImageUrl()).isEqualTo("수정");
    }

    @Test
    public void 스터디_정보_변경_시_null_값은_기존_값을_유지한다() {
        // given
        Study study = StudyFixture.createNormalStudy();
        String studyName = study.getName();

        // when
        study.updateInformation(null, null, null, null, null);

        // then
        Assertions.assertThat(study.getName()).isEqualTo(studyName);
    }

    @Test
    public void 스터디_최대_인원을_변경한다() {
        //given
        Study study = StudyFixture.createNormalStudy();
        int validMemberLimit = study.getMemberLimit() + 1;

        // when
        study.updateMemberLimit(validMemberLimit);

        // then
        Assertions.assertThat(study.getMemberLimit()).isEqualTo(validMemberLimit);
    }

    @Test
    public void 스터디_현재_참여_인원과_같은_최대_인원으로_변경할_수_있다() {
        // given
        Study study = StudyFixture.createNormalStudy();
        study.increaseMemberCount();
        int memberCount = study.getMemberCount();

        // when
        study.updateMemberLimit(memberCount);

        // then
        Assertions.assertThat(study.getMemberLimit()).isEqualTo(memberCount);
    }

    @Test
    void 최대_인원_변경_실패_시_기존_값은_유지된다() {
        Study study = StudyFixture.createNormalStudy();
        int memberLimit = study.getMemberLimit();

        Assertions.assertThatThrownBy(() -> study.updateMemberLimit(1))
                .isInstanceOf(StudyMemberLimitOutOfRangeException.class);

        Assertions.assertThat(study.getMemberLimit()).isEqualTo(memberLimit);
    }

    @Test
    public void 스터디_멤버를_추가한다() {
        //given
        Study study = StudyFixture.createNormalStudy();
        int currentMemberCount = study.getMemberCount();

        // when
        study.increaseMemberCount();

        // then
        Assertions.assertThat(study.getMemberCount())
                .isEqualTo(currentMemberCount + 1);
    }

    @Test
    public void 스터디_멤버를_제거한다() {
        //given
        Study study = StudyFixture.createNormalStudy();
        study.increaseMemberCount();
        int currentMemberCount = study.getMemberCount();

        // when
        study.decreaseMemberCount();

        // then
        Assertions.assertThat(study.getMemberCount())
                .isEqualTo(currentMemberCount - 1);
    }

    @Test
    public void 스터디_비밀번호가_존재하는_경우_검증에_통과한다() {
        //given
        Study study = StudyFixture.createNormalStudyWithPassword();
        String password = study.getPassword();

        // when & then
        Assertions.assertThatCode(() -> study.validatePassword(password))
                .doesNotThrowAnyException();
    }

    @Test
    public void 챌린지_스터디인_경우_true를_반환한다() {
        //given
        Study study = StudyFixture.createChallengeStudy();

        // when & then
        Assertions.assertThat(study.isChallengeStudy()).isTrue();
    }

    @Test
    public void 일반_스터디인_경우_false를_반환한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        // when & then
        Assertions.assertThat(study.isChallengeStudy()).isFalse();
    }

    @Test
    public void 스터디_비밀번호가_존재하는_경우_true를_반환한다() {
        //given
        Study study = StudyFixture.createNormalStudyWithPassword();

        // when & then
        Assertions.assertThat(study.hasPassword()).isTrue();
    }

    @Test
    public void 스터디_비밀번호가_존재하지_않는_경우_false를_반환한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        // when & then
        Assertions.assertThat(study.hasPassword()).isFalse();
    }

    @Test
    public void 스터디_이미지_URL_변경_시_기존_URL을_반환한다() {
        Study study = StudyFixture.createNormalStudyWithImage();
        String imageUrl = study.getImageUrl();
        String oldImage = study.changeImageUrl("new");

        Assertions.assertThat(oldImage).isEqualTo(imageUrl);
        Assertions.assertThat(study.getImageUrl()).isEqualTo("new");
    }

    @Test
    public void 스터디가_생성된_지_7일_이내인_경우_true를_반환한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(study, "createdAt", now.minusDays(3));

        // when & then
        Assertions.assertThat(study.isNewlyCreated()).isTrue();
    }

    @Test
    public void 스터디가_생성된_지_7일보다_오래된_경우_false를_반환한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(study, "createdAt", now.minusDays(10));

        // when & then
        Assertions.assertThat(study.isNewlyCreated()).isFalse();
    }

    @Test
    public void 스터디의_목표_시간보다_일일_공부량이_많거나_같은_경우_true를_반환한다() {
        //given
        Study study = StudyFixture.createChallengeStudy();
        DailyStudySummary dailyStudySummary = DailyStudySummaryFixture.createDailyStudySummary();

        // when & then
        Assertions.assertThat(study.isAchieved(dailyStudySummary)).isTrue();
    }

    @Test
    public void 스터디의_목표_시간이_일일_공부량보다_많은_경우_false를_반환한다() {
        //given
        Study study = StudyFixture.createChallengeStudy();
        DailyStudySummary dailyStudySummary = DailyStudySummaryFixture.createDailyStudySummaryWithStudyTime(1L * 3600L);

        // when & then
        Assertions.assertThat(study.isAchieved(dailyStudySummary)).isFalse();
    }

    @Test
    public void 스터디_생성_시_최대_인원이_최댓값_초과인_경우_예외가_발생한다() {
        //given
        int validMaximumMemberLimit = 30;
        int invalidMaximumMemberLimit = validMaximumMemberLimit + 1;

        // when & then
        Assertions.assertThatThrownBy(() -> {
            Study.builder()
                    .name("일반 스터디")
                    .description("일반 스터디 생성")
                    .tag(StudyTag.SCHOOL)
                    .tier("티어")
                    .memberLimit(invalidMaximumMemberLimit)
                    .imageUrl(null)
                    .type(StudyType.NORMAL)
                    .build();
        }).isInstanceOf(StudyMemberLimitOutOfRangeException.class);
    }

    @Test
    public void 스터디_생성_시_최대_인원이_최솟값_미만인_경우_예외가_발생한다() {
        //given
        int validMinimumMemberLimit = 2;
        int invalidMinimumMemberLimit = validMinimumMemberLimit - 1;

        // when & then
        Assertions.assertThatThrownBy(() -> {
            Study.builder()
                    .name("일반 스터디")
                    .description("일반 스터디 생성")
                    .tag(StudyTag.SCHOOL)
                    .tier("티어")
                    .memberLimit(invalidMinimumMemberLimit)
                    .imageUrl(null)
                    .type(StudyType.NORMAL)
                    .build();
        }).isInstanceOf(StudyMemberLimitOutOfRangeException.class);
    }

    @Test
    public void 스터디의_현재_참여_인원보다_적은_최대_인원으로_변경할_경우_예외가_발생한다() {
        //given
        Study study = StudyFixture.createNormalStudy();
        study.increaseMemberCount();
        study.increaseMemberCount();
        int invalidMemberLimit = study.getMemberCount() - 1;

        // when & then
        Assertions.assertThatThrownBy(() -> study.updateMemberLimit(invalidMemberLimit))
                .isInstanceOf(InvalidStudyMemberLimitException.class);
    }

    @Test
    public void 스터디_최대_인원_변경_시_최댓값_초과인_경우_예외가_발생한다() {
        //given
        int validMaximumMemberLimit = 30;
        int invalidMaximumMemberLimit = validMaximumMemberLimit + 1;
        Study study = StudyFixture.createNormalStudy();

        // when & then
        Assertions.assertThatThrownBy(() -> study.updateMemberLimit(invalidMaximumMemberLimit))
                .isInstanceOf(StudyMemberLimitOutOfRangeException.class);
    }

    @Test
    public void 스터디_최대_인원_변경_시_최솟값_미만인_경우_예외가_발생한다() {
        //given
        int validMinimumMemberLimit = 2;
        int invalidMinimumMemberLimit = validMinimumMemberLimit - 1;
        Study study = StudyFixture.createNormalStudy();

        // when & then
        Assertions.assertThatThrownBy(() -> study.updateMemberLimit(invalidMinimumMemberLimit))
                .isInstanceOf(StudyMemberLimitOutOfRangeException.class);
    }

    @Test
    public void 스터디_멤버_추가_시_최대_인원을_초과하는_경우_예외가_발생한다() {
        //given
        Study study = StudyFixture.createNormalStudy();
        int capacity = study.getMemberLimit();

        for (int i = 1; i < capacity; i++) {
            study.increaseMemberCount();
        }

        // when & then
        Assertions.assertThatThrownBy(study::increaseMemberCount)
                .isInstanceOf(StudyMemberCountExceededException.class);
    }

    @Test
    public void 스터디_멤버_제거_시_현재_멤버가_1명_이하인_경우_예외가_발생한다() {
        //given
        Study study = StudyFixture.createNormalStudy();

        // when & then
        Assertions.assertThatThrownBy(study::decreaseMemberCount)
                .isInstanceOf(StudyMinimumMemberRequiredException.class);
    }

    @Test
    public void 스터디_비밀번호가_존재할_때_값을_입력하지_않는_경우_예외가_발생한다() {
        //given
        Study study = StudyFixture.createNormalStudyWithPassword();
        String password = null;

        // when & then
        Assertions.assertThatThrownBy(() -> study.validatePassword(password))
                .isInstanceOf(StudyPasswordRequiredException.class);
    }

    @Test
    public void 스터디_비밀번호가_존재할_때_입력값과_일치하지_않는_경우_예외가_발생한다() {
        //given
        Study study = StudyFixture.createNormalStudyWithPassword();
        String password = "1111";

        // when & then
        Assertions.assertThatThrownBy(() -> study.validatePassword(password))
                .isInstanceOf(StudyPasswordMismatchException.class);
    }

}
