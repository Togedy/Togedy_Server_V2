package com.togedy.togedy_server_v2.domain.config.api.e2e;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.togedy.togedy_server_v2.domain.config.dao.CalendarAnnouncementRepository;
import com.togedy.togedy_server_v2.domain.config.entity.CalendarAnnouncement;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("E2E 테스트")
public class CalendarAnnouncementE2ETest extends AbstractE2ETest {

    @Autowired
    CalendarAnnouncementRepository calendarAnnouncementRepository;

    @Test
    @DisplayName("등록된 캘린더 공지가 없으면 공지 없음으로 응답한다.")
    public void readAnnouncementWhenEmpty() throws Exception {
        mockMvc.perform(get("/api/v2/calendars/announcement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.hasAnnouncement").value(false))
                .andExpect(jsonPath("$.response.announcement").doesNotExist());
    }

    @Test
    @DisplayName("캘린더 공지 중 가장 최근에 등록된 공지를 응답한다.")
    public void readLatestAnnouncement() throws Exception {
        calendarAnnouncementRepository.save(CalendarAnnouncement.builder().userId(1L).content("이전 공지").build());
        calendarAnnouncementRepository.save(CalendarAnnouncement.builder().userId(1L).content("최신 공지").build());

        mockMvc.perform(get("/api/v2/calendars/announcement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.hasAnnouncement").value(true))
                .andExpect(jsonPath("$.response.announcement").value("최신 공지"));
    }
}
