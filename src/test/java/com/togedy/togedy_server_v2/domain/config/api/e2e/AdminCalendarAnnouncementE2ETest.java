package com.togedy.togedy_server_v2.domain.config.api.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.togedy.togedy_server_v2.domain.config.dao.CalendarAnnouncementRepository;
import com.togedy.togedy_server_v2.domain.config.entity.CalendarAnnouncement;
import com.togedy.togedy_server_v2.global.security.AdminUser;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("E2E 테스트")
public class AdminCalendarAnnouncementE2ETest extends AbstractE2ETest {

    private static final AdminUser ADMIN = AdminUser.builder().id(1L).loginId("admin").build();

    @Autowired
    CalendarAnnouncementRepository calendarAnnouncementRepository;

    @Test
    @DisplayName("캘린더 공지 목록 페이지를 조회한다.")
    public void readCalendarAnnouncementList() throws Exception {
        calendarAnnouncementRepository.save(CalendarAnnouncement.builder().userId(1L).content("공지 내용").build());

        mockMvc.perform(get("/admin/calendar-announcements").with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("공지 내용")));
    }

    @Test
    @DisplayName("캘린더 공지를 등록하면 앱 캘린더 공지 API에 노출된다.")
    public void createCalendarAnnouncement() throws Exception {
        mockMvc.perform(post("/admin/calendar-announcements")
                        .param("content", "새 공지")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/calendar-announcements"));

        assertThat(calendarAnnouncementRepository.findAll())
                .singleElement()
                .satisfies(announcement -> {
                    assertThat(announcement.getContent()).isEqualTo("새 공지");
                    assertThat(announcement.getUserId()).isEqualTo(ADMIN.getId());
                });

        mockMvc.perform(get("/api/v2/calendars/announcement"))
                .andExpect(jsonPath("$.response.announcement").value("새 공지"));
    }

    @Test
    @DisplayName("내용이 비어 있으면 캘린더 공지를 등록하지 않고 폼을 다시 보여준다.")
    public void createBlankCalendarAnnouncement() throws Exception {
        mockMvc.perform(post("/admin/calendar-announcements")
                        .param("content", " ")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "content"));

        assertThat(calendarAnnouncementRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("캘린더 공지 수정 폼에 기존 내용을 채워 보여준다.")
    public void readEditForm() throws Exception {
        CalendarAnnouncement announcement = calendarAnnouncementRepository.save(
                CalendarAnnouncement.builder().userId(1L).content("기존 공지").build());

        mockMvc.perform(get("/admin/calendar-announcements/{id}/edit", announcement.getId()).with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("기존 공지")));
    }

    @Test
    @DisplayName("캘린더 공지를 수정한다.")
    public void modifyCalendarAnnouncement() throws Exception {
        CalendarAnnouncement announcement = calendarAnnouncementRepository.save(
                CalendarAnnouncement.builder().userId(1L).content("기존 공지").build());

        mockMvc.perform(post("/admin/calendar-announcements/{id}/edit", announcement.getId())
                        .param("content", "수정된 공지")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/calendar-announcements"));

        assertThat(calendarAnnouncementRepository.findById(announcement.getId()))
                .get()
                .extracting(CalendarAnnouncement::getContent)
                .isEqualTo("수정된 공지");
    }

    @Test
    @DisplayName("캘린더 공지를 삭제한다.")
    public void removeCalendarAnnouncement() throws Exception {
        CalendarAnnouncement announcement = calendarAnnouncementRepository.save(
                CalendarAnnouncement.builder().userId(1L).content("기존 공지").build());

        mockMvc.perform(post("/admin/calendar-announcements/{id}/delete", announcement.getId())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/calendar-announcements"));

        assertThat(calendarAnnouncementRepository.findAll()).isEmpty();
    }
}
