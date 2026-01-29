package com.togedy.togedy_server_v2.domain.planner.dao;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.dto.DailyStudyTimeDto;
import com.togedy.togedy_server_v2.global.fixtures.DailyStudySummaryFixture;
import com.togedy.togedy_server_v2.global.support.AbstractRepositoryTest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DailyStudySummaryRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private DailyStudySummaryRepository dailyStudySummaryRepository;

    private static LocalDate DEFAULT_DATE = LocalDate.of(2026, 1, 1);

    @Test
    public void 여러_유저_ID로_특정_날짜의_공부_요약을_조회한다() {
        //given
        DailyStudySummary dailyStudySummary1 = DailyStudySummaryFixture.createDailyStudySummaryWithUserId(1L);
        dailyStudySummaryRepository.save(dailyStudySummary1);

        DailyStudySummary dailyStudySummary2 = DailyStudySummaryFixture.createDailyStudySummaryWithUserId(2L);
        dailyStudySummaryRepository.save(dailyStudySummary2);

        // when
        List<DailyStudySummary> result = dailyStudySummaryRepository.findAllByUserIdsAndDate(
                List.of(dailyStudySummary1.getUserId(), dailyStudySummary2.getUserId()), DEFAULT_DATE);

        // then
        Assertions.assertThat(result).hasSize(2).extracting(DailyStudySummary::getId)
                .contains(dailyStudySummary1.getId(), dailyStudySummary2.getId());
    }

    @Test
    public void 다른_날짜의_공부_요약은_조회되지_않는다() {
        //given
        DailyStudySummary dailyStudySummary = DailyStudySummaryFixture.createDailyStudySummaryWithDate(DEFAULT_DATE);
        dailyStudySummaryRepository.save(dailyStudySummary);

        DailyStudySummary dailyStudySummary1 = DailyStudySummaryFixture.createDailyStudySummaryWithDate(
                DEFAULT_DATE.minusDays(1));
        dailyStudySummaryRepository.save(dailyStudySummary1);

        // when
        List<DailyStudySummary> result = dailyStudySummaryRepository.findAllByUserIdsAndDate(
                List.of(dailyStudySummary.getUserId()), DEFAULT_DATE);

        // then
        Assertions.assertThat(result).hasSize(1).extracting(DailyStudySummary::getId)
                .containsExactly(dailyStudySummary.getId());
    }

    @Test
    public void 유저_ID와_날짜로_공부_요약을_단건_조회한다() {
        //given
        DailyStudySummary dailyStudySummary = DailyStudySummaryFixture.createDailyStudySummary();
        dailyStudySummaryRepository.save(dailyStudySummary);

        // when
        Optional<DailyStudySummary> result = dailyStudySummaryRepository.findByUserIdAndDate(
                dailyStudySummary.getUserId(), dailyStudySummary.getDate());

        // then
        Assertions.assertThat(result).isPresent().get().extracting(DailyStudySummary::getId)
                .isEqualTo(dailyStudySummary.getId());
    }

    @Test
    public void 해당_날짜에_공부_요약이_존재하지_않는_경우_빈_Optional이_반환된다() {
        //given
        DailyStudySummary dailyStudySummary = DailyStudySummaryFixture.createDailyStudySummary();
        dailyStudySummaryRepository.save(dailyStudySummary);

        // when
        Optional<DailyStudySummary> result = dailyStudySummaryRepository.findByUserIdAndDate(
                dailyStudySummary.getUserId(), dailyStudySummary.getDate().minusDays(1));

        // then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void 유저의_모든_공부_시간의_총합을_계산한다() {
        //given
        DailyStudySummary dailyStudySummary1 = DailyStudySummaryFixture.createDailyStudySummaryWithStudyTime(3000L);
        dailyStudySummaryRepository.save(dailyStudySummary1);

        DailyStudySummary dailyStudySummary2 = DailyStudySummaryFixture.createDailyStudySummaryWithStudyTime(2000L);
        dailyStudySummaryRepository.save(dailyStudySummary2);

        DailyStudySummary dailyStudySummary3 = DailyStudySummaryFixture.createDailyStudySummaryWithStudyTime(1000L);
        dailyStudySummaryRepository.save(dailyStudySummary3);

        Long totalStudyTime = dailyStudySummary1.getStudyTime() + dailyStudySummary2.getStudyTime()
                + dailyStudySummary3.getStudyTime();

        // when
        Optional<Long> result = dailyStudySummaryRepository.findTotalStudyTimeByUserId(dailyStudySummary1.getUserId());

        // then
        Assertions.assertThat(result).isPresent().get().isEqualTo(totalStudyTime);
    }

    @Test
    public void 유저의_공부_요약이_존재하지_않는_경우_공부_시간의_총합은_빈_Optional을_반환한다() {
        // when & then
        Assertions.assertThat(dailyStudySummaryRepository.findTotalStudyTimeByUserId(1L)).isEmpty();
    }

    @Test
    public void 시작일과_종료일을_모두_포함하여_조회한다() {
        //given
        LocalDate startDate = DEFAULT_DATE.minusDays(10);
        LocalDate endDate = DEFAULT_DATE;

        DailyStudySummary startDateDailyStudySummary = DailyStudySummaryFixture.createDailyStudySummaryWithDate(
                startDate);
        dailyStudySummaryRepository.save(startDateDailyStudySummary);

        DailyStudySummary endDateDailyStudySummary = DailyStudySummaryFixture.createDailyStudySummaryWithDate(endDate);
        dailyStudySummaryRepository.save(endDateDailyStudySummary);

        // when
        List<DailyStudySummary> result = dailyStudySummaryRepository.findAllByUserIdAndPeriod(
                startDateDailyStudySummary.getUserId(), startDate, endDate);

        // then
        Assertions.assertThat(result).hasSize(2).extracting(DailyStudySummary::getId)
                .contains(startDateDailyStudySummary.getId(), endDateDailyStudySummary.getId());
    }

    @Test
    public void 기간_범위를_벗어난_공부_요약은_조회되지_않는다() {
        //given
        LocalDate startDate = DEFAULT_DATE.minusDays(10);
        LocalDate endDate = DEFAULT_DATE;

        DailyStudySummary startDateDailyStudySummary = DailyStudySummaryFixture.createDailyStudySummaryWithDate(
                startDate.minusDays(1));
        dailyStudySummaryRepository.save(startDateDailyStudySummary);

        DailyStudySummary endDateDailyStudySummary = DailyStudySummaryFixture.createDailyStudySummaryWithDate(endDate);
        dailyStudySummaryRepository.save(endDateDailyStudySummary);

        // when
        List<DailyStudySummary> result = dailyStudySummaryRepository.findAllByUserIdAndPeriod(
                startDateDailyStudySummary.getUserId(), startDate, endDate);

        // then
        Assertions.assertThat(result).hasSize(1).extracting(DailyStudySummary::getId)
                .containsExactly(endDateDailyStudySummary.getId());
    }

    @Test
    public void 여러_유저의_기간별_일일_공부시간을_DTO로_조회한다() {
        //given
        DailyStudySummary dailyStudySummary1 = DailyStudySummaryFixture.createDailyStudySummaryWithUserId(1L);
        dailyStudySummaryRepository.save(dailyStudySummary1);

        DailyStudySummary dailyStudySummary2 = DailyStudySummaryFixture.createDailyStudySummaryWithUserId(2L);
        dailyStudySummaryRepository.save(dailyStudySummary2);

        DailyStudySummary dailyStudySummary3 = DailyStudySummaryFixture.createDailyStudySummaryWithUserId(3L);
        dailyStudySummaryRepository.save(dailyStudySummary3);

        // when
        List<DailyStudyTimeDto> result = dailyStudySummaryRepository.findDailyStudyTimeByUserIdsAndPeriod(
                List.of(dailyStudySummary1.getUserId(), dailyStudySummary2.getUserId(), dailyStudySummary3.getUserId()),
                DEFAULT_DATE, DEFAULT_DATE);

        // then
        Assertions.assertThat(result).hasSize(3)
                .extracting(DailyStudyTimeDto::getUserId, DailyStudyTimeDto::getDate, DailyStudyTimeDto::getStudyTime)
                .containsExactlyInAnyOrder(tuple(dailyStudySummary1.getUserId(), dailyStudySummary1.getDate(),
                                dailyStudySummary1.getStudyTime()),
                        tuple(dailyStudySummary2.getUserId(), dailyStudySummary2.getDate(),
                                dailyStudySummary2.getStudyTime()),
                        tuple(dailyStudySummary3.getUserId(), dailyStudySummary3.getDate(),
                                dailyStudySummary3.getStudyTime()));
    }

    @Test
    public void 특정_기간의_공부시간의_합을_구해_DTO로_조회한다() {
        //given
        DailyStudySummary dailyStudySummary1 = DailyStudySummaryFixture.createDailyStudySummaryWithStudyTime(3000L);
        dailyStudySummaryRepository.save(dailyStudySummary1);

        DailyStudySummary dailyStudySummary2 = DailyStudySummaryFixture.createDailyStudySummaryWithStudyTime(3000L);
        dailyStudySummaryRepository.save(dailyStudySummary2);

        // when
        List<DailyStudyTimeDto> result = dailyStudySummaryRepository.findDailyStudyTimeByUserIdsAndPeriod(
                List.of(dailyStudySummary1.getUserId()), DEFAULT_DATE, DEFAULT_DATE);

        // then
        Assertions.assertThat(result).hasSize(1);

        Assertions.assertThat(result.get(0).getStudyTime())
                .isEqualTo(dailyStudySummary1.getStudyTime() + dailyStudySummary2.getStudyTime());
    }
}
