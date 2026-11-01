package com.togedy.togedy_server_v2.domain.badge.api.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.togedy.togedy_server_v2.domain.badge.dao.HolidayKindRepository;
import com.togedy.togedy_server_v2.domain.badge.dao.HolidayRepository;
import com.togedy.togedy_server_v2.domain.badge.entity.Holiday;
import com.togedy.togedy_server_v2.domain.badge.entity.HolidayKind;
import com.togedy.togedy_server_v2.global.security.AdminUser;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("E2E 테스트")
public class AdminHolidayE2ETest extends AbstractE2ETest {

    private static final AdminUser ADMIN = AdminUser.builder().id(1L).loginId("admin").build();

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    HolidayKindRepository holidayKindRepository;

    HolidayKind chuseok;

    @BeforeEach
    void setUpHolidayKind() {
        chuseok = holidayKindRepository.save(HolidayKind.builder().name("추석").badgeEnabled(true).build());
    }

    @Test
    @DisplayName("연휴 목록 페이지를 조회한다.")
    public void readHolidayList() throws Exception {
        LocalDate today = TimeUtil.currentStudyDate();
        holidayRepository.save(holiday(today.plusDays(10), today.plusDays(12)));

        mockMvc.perform(get("/admin/holidays").with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("추석")));
    }

    @Test
    @DisplayName("연휴를 등록한다.")
    public void createHoliday() throws Exception {
        LocalDate today = TimeUtil.currentStudyDate();

        mockMvc.perform(post("/admin/holidays")
                        .param("holidayKindId", chuseok.getId().toString())
                        .param("startDate", today.plusDays(10).toString())
                        .param("endDate", today.plusDays(12).toString())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/holidays"));

        assertThat(holidayRepository.findAll())
                .singleElement()
                .satisfies(holiday -> {
                    assertThat(holiday.getHolidayKind().getId()).isEqualTo(chuseok.getId());
                    assertThat(holiday.getStartDate()).isEqualTo(today.plusDays(10));
                    assertThat(holiday.getEndDate()).isEqualTo(today.plusDays(12));
                });
    }

    @Test
    @DisplayName("종료일이 시작일보다 앞서면 연휴를 등록하지 않고 폼을 다시 보여준다.")
    public void createHolidayWithInvalidPeriod() throws Exception {
        LocalDate today = TimeUtil.currentStudyDate();

        mockMvc.perform(post("/admin/holidays")
                        .param("holidayKindId", chuseok.getId().toString())
                        .param("startDate", today.plusDays(12).toString())
                        .param("endDate", today.plusDays(10).toString())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("form"));

        assertThat(holidayRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 연휴 종류로는 연휴를 등록하지 않고 폼을 다시 보여준다.")
    public void createHolidayWithUnknownKind() throws Exception {
        LocalDate today = TimeUtil.currentStudyDate();

        mockMvc.perform(post("/admin/holidays")
                        .param("holidayKindId", String.valueOf(chuseok.getId() + 1))
                        .param("startDate", today.plusDays(10).toString())
                        .param("endDate", today.plusDays(12).toString())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("form"));

        assertThat(holidayRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("이미 종료된 기간으로는 연휴를 등록하지 않는다.")
    public void createEndedHoliday() throws Exception {
        LocalDate today = TimeUtil.currentStudyDate();

        mockMvc.perform(post("/admin/holidays")
                        .param("holidayKindId", chuseok.getId().toString())
                        .param("startDate", today.minusDays(5).toString())
                        .param("endDate", today.minusDays(3).toString())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("form"));

        assertThat(holidayRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("기간이 겹치는 연휴는 등록하지 않는다.")
    public void createOverlappingHoliday() throws Exception {
        LocalDate today = TimeUtil.currentStudyDate();
        holidayRepository.save(holiday(today.plusDays(10), today.plusDays(12)));

        mockMvc.perform(post("/admin/holidays")
                        .param("holidayKindId", chuseok.getId().toString())
                        .param("startDate", today.plusDays(12).toString())
                        .param("endDate", today.plusDays(14).toString())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("form"));

        assertThat(holidayRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("판정 전인 연휴를 수정한다.")
    public void modifyHoliday() throws Exception {
        LocalDate today = TimeUtil.currentStudyDate();
        Holiday holiday = holidayRepository.save(holiday(today.plusDays(10), today.plusDays(12)));

        mockMvc.perform(post("/admin/holidays/{id}/edit", holiday.getId())
                        .param("holidayKindId", chuseok.getId().toString())
                        .param("startDate", today.plusDays(10).toString())
                        .param("endDate", today.plusDays(13).toString())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/holidays"));

        assertThat(holidayRepository.findById(holiday.getId()))
                .get()
                .extracting(Holiday::getEndDate)
                .isEqualTo(today.plusDays(13));
    }

    @Test
    @DisplayName("판정이 완료된 연휴는 수정하지 않는다.")
    public void modifyEndedHoliday() throws Exception {
        LocalDate today = TimeUtil.currentStudyDate();
        Holiday holiday = holidayRepository.save(holiday(today.minusDays(5), today.minusDays(3)));

        mockMvc.perform(post("/admin/holidays/{id}/edit", holiday.getId())
                        .param("holidayKindId", chuseok.getId().toString())
                        .param("startDate", today.plusDays(1).toString())
                        .param("endDate", today.plusDays(3).toString())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("form"));

        assertThat(holidayRepository.findById(holiday.getId()))
                .get()
                .extracting(Holiday::getStartDate)
                .isEqualTo(today.minusDays(5));
    }

    @Test
    @DisplayName("판정 전인 연휴를 삭제한다.")
    public void removeHoliday() throws Exception {
        LocalDate today = TimeUtil.currentStudyDate();
        Holiday holiday = holidayRepository.save(holiday(today.plusDays(10), today.plusDays(12)));

        mockMvc.perform(post("/admin/holidays/{id}/delete", holiday.getId())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/holidays"));

        assertThat(holidayRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("판정이 완료된 연휴는 삭제하지 않는다.")
    public void removeEndedHoliday() throws Exception {
        LocalDate today = TimeUtil.currentStudyDate();
        Holiday holiday = holidayRepository.save(holiday(today.minusDays(5), today.minusDays(3)));

        mockMvc.perform(post("/admin/holidays/{id}/delete", holiday.getId())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/holidays"))
                .andExpect(flash().attributeExists("errorMessage"));

        assertThat(holidayRepository.findAll()).hasSize(1);
    }

    private Holiday holiday(LocalDate startDate, LocalDate endDate) {
        return Holiday.builder().holidayKind(chuseok).startDate(startDate).endDate(endDate).build();
    }
}
