package com.togedy.togedy_server_v2.domain.support.api.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.togedy.togedy_server_v2.domain.support.dao.InquiryRepository;
import com.togedy.togedy_server_v2.domain.support.entity.Inquiry;
import com.togedy.togedy_server_v2.domain.support.enums.InquiryStatus;
import com.togedy.togedy_server_v2.domain.support.enums.InquiryType;
import com.togedy.togedy_server_v2.global.security.AdminUser;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("E2E 테스트")
public class AdminInquiryE2ETest extends AbstractE2ETest {

    private static final AdminUser ADMIN = AdminUser.builder().id(1L).loginId("admin").build();

    @Autowired
    InquiryRepository inquiryRepository;

    @Test
    @DisplayName("문의 목록 페이지에서 문의한 유저 ID와 내용을 조회한다.")
    public void readInquiryList() throws Exception {
        saveInquiry(777L);

        mockMvc.perform(get("/admin/inquiries").with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("777")))
                .andExpect(content().string(Matchers.containsString("문의 내용")));
    }

    @Test
    @DisplayName("문의 상세 페이지에서 내용과 회신 이메일을 조회한다.")
    public void readInquiryDetail() throws Exception {
        Inquiry inquiry = saveInquiry(777L);

        mockMvc.perform(get("/admin/inquiries/{id}", inquiry.getId()).with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("문의 내용")))
                .andExpect(content().string(Matchers.containsString("reply@test.com")));
    }

    @Test
    @DisplayName("존재하지 않는 문의의 상세 페이지를 조회하면 404를 응답한다.")
    public void readNotFoundInquiryDetail() throws Exception {
        mockMvc.perform(get("/admin/inquiries/{id}", 999L).with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상세 페이지에서 문의 상태를 변경하면 상세 페이지로 돌아간다.")
    public void modifyInquiryStatusFromDetail() throws Exception {
        Inquiry inquiry = saveInquiry(1L);

        mockMvc.perform(post("/admin/inquiries/{id}/status", inquiry.getId())
                        .param("status", InquiryStatus.ANSWERED.name())
                        .param("page", "2")
                        .param("detail", "true")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/inquiries/" + inquiry.getId() + "?page=2"));
    }

    @Test
    @DisplayName("문의 상태를 답변 완료로 변경한다.")
    public void modifyInquiryStatus() throws Exception {
        Inquiry inquiry = saveInquiry(1L);

        mockMvc.perform(post("/admin/inquiries/{id}/status", inquiry.getId())
                        .param("status", InquiryStatus.ANSWERED.name())
                        .param("page", "2")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/inquiries?page=2"));

        assertThat(inquiryRepository.findById(inquiry.getId()))
                .get()
                .extracting(Inquiry::getStatus)
                .isEqualTo(InquiryStatus.ANSWERED);
    }

    @Test
    @DisplayName("존재하지 않는 문의의 상태를 변경하면 404를 응답한다.")
    public void modifyNotFoundInquiryStatus() throws Exception {
        mockMvc.perform(post("/admin/inquiries/{id}/status", 999L)
                        .param("status", InquiryStatus.ANSWERED.name())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    private Inquiry saveInquiry(Long userId) {
        return inquiryRepository.save(Inquiry.builder()
                .userId(userId)
                .inquiryType(InquiryType.BUG)
                .content("문의 내용")
                .replyEmail("reply@test.com")
                .build());
    }
}
