package com.togedy.togedy_server_v2.domain.support.api.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.togedy.togedy_server_v2.domain.support.dao.NoticeRepository;
import com.togedy.togedy_server_v2.domain.support.entity.Notice;
import com.togedy.togedy_server_v2.global.security.AdminUser;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("E2E 테스트")
public class AdminNoticeE2ETest extends AbstractE2ETest {

    private static final AdminUser ADMIN = AdminUser.builder().id(1L).loginId("admin").build();

    @Autowired
    NoticeRepository noticeRepository;

    @Test
    @DisplayName("앱 공지사항 목록 페이지를 조회한다.")
    public void readNoticeList() throws Exception {
        saveNotice();

        mockMvc.perform(get("/admin/notices").with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("기존 제목")));
    }

    @Test
    @DisplayName("앱 공지사항 상세 페이지에서 제목과 내용을 조회한다.")
    public void readNoticeDetail() throws Exception {
        Notice notice = saveNotice();

        mockMvc.perform(get("/admin/notices/{id}", notice.getId()).with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("기존 제목")))
                .andExpect(content().string(Matchers.containsString("기존 내용")));
    }

    @Test
    @DisplayName("존재하지 않는 앱 공지사항의 상세 페이지를 조회하면 404를 응답한다.")
    public void readNotFoundNoticeDetail() throws Exception {
        mockMvc.perform(get("/admin/notices/{id}", 999L).with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("앱 공지사항 작성 폼을 조회한다.")
    public void readCreateForm() throws Exception {
        mockMvc.perform(get("/admin/notices/new").with(user(ADMIN)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("앱 공지사항을 등록하면 작성자는 로그인한 관리자로 기록된다.")
    public void createNotice() throws Exception {
        mockMvc.perform(post("/admin/notices")
                        .param("noticeTitle", "새 제목")
                        .param("noticeContent", "새 내용")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/notices"));

        assertThat(noticeRepository.findAll())
                .singleElement()
                .satisfies(notice -> {
                    assertThat(notice.getTitle()).isEqualTo("새 제목");
                    assertThat(notice.getContent()).isEqualTo("새 내용");
                    assertThat(notice.getUserId()).isEqualTo(ADMIN.getId());
                });
    }

    @Test
    @DisplayName("제목이 비어 있으면 앱 공지사항을 등록하지 않고 폼을 다시 보여준다.")
    public void createBlankNotice() throws Exception {
        mockMvc.perform(post("/admin/notices")
                        .param("noticeTitle", "")
                        .param("noticeContent", "새 내용")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "noticeTitle"));

        assertThat(noticeRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("앱 공지사항 수정 폼에 기존 내용을 채워 보여준다.")
    public void readEditForm() throws Exception {
        Notice notice = saveNotice();

        mockMvc.perform(get("/admin/notices/{id}/edit", notice.getId()).with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("기존 제목")))
                .andExpect(content().string(Matchers.containsString("기존 내용")));
    }

    @Test
    @DisplayName("앱 공지사항을 수정한다.")
    public void modifyNotice() throws Exception {
        Notice notice = saveNotice();

        mockMvc.perform(post("/admin/notices/{id}/edit", notice.getId())
                        .param("noticeTitle", "수정된 제목")
                        .param("noticeContent", "수정된 내용")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/notices"));

        Notice modified = noticeRepository.findById(notice.getId()).orElseThrow();
        assertThat(modified.getTitle()).isEqualTo("수정된 제목");
        assertThat(modified.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("앱 공지사항을 삭제한다.")
    public void removeNotice() throws Exception {
        Notice notice = saveNotice();

        mockMvc.perform(post("/admin/notices/{id}/delete", notice.getId())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/notices"));

        assertThat(noticeRepository.findAll()).isEmpty();
    }

    private Notice saveNotice() {
        return noticeRepository.save(Notice.builder().userId(1L).title("기존 제목").content("기존 내용").build());
    }
}
