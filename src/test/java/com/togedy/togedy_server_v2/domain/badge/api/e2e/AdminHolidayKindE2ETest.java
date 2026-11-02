package com.togedy.togedy_server_v2.domain.badge.api.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
import com.togedy.togedy_server_v2.global.enums.ImageCategory;
import com.togedy.togedy_server_v2.global.security.AdminUser;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@Tag("E2E 테스트")
public class AdminHolidayKindE2ETest extends AbstractE2ETest {

    private static final AdminUser ADMIN = AdminUser.builder().id(1L).loginId("admin").build();
    private static final String OLD_IMAGE_URL = "https://old-s3/chuseok.png";
    private static final String UPLOADED_IMAGE_URL = "https://mock-s3/test.png";

    @Autowired
    HolidayKindRepository holidayKindRepository;

    @Autowired
    HolidayRepository holidayRepository;

    @Test
    @DisplayName("연휴 종류 목록 페이지를 조회한다.")
    public void readHolidayKindList() throws Exception {
        holidayKindRepository.save(holidayKind("추석", OLD_IMAGE_URL, true));

        mockMvc.perform(get("/admin/holiday-kinds").with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("추석")));
    }

    @Test
    @DisplayName("뱃지 이미지를 업로드해 연휴 종류를 등록한다.")
    public void createHolidayKind() throws Exception {
        mockMvc.perform(multipart("/admin/holiday-kinds")
                        .file(badgeImage())
                        .param("name", "추석")
                        .param("badgeEnabled", "true")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/holiday-kinds"));

        verify(s3Service).uploadFile(any(MultipartFile.class), any(ImageCategory.class));
        assertThat(holidayKindRepository.findAll())
                .singleElement()
                .satisfies(kind -> {
                    assertThat(kind.getName()).isEqualTo("추석");
                    assertThat(kind.getBadgeImageUrl()).isEqualTo(UPLOADED_IMAGE_URL);
                    assertThat(kind.isBadgeEnabled()).isTrue();
                });
    }

    @Test
    @DisplayName("뱃지를 지급하는데 뱃지 이미지가 없으면 연휴 종류를 등록하지 않고 폼을 다시 보여준다.")
    public void createBadgeEnabledHolidayKindWithoutImage() throws Exception {
        mockMvc.perform(multipart("/admin/holiday-kinds")
                        .param("name", "추석")
                        .param("badgeEnabled", "true")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("form"));

        assertThat(holidayKindRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("같은 이름의 연휴 종류는 등록하지 않고, 뱃지 이미지도 업로드하지 않는다.")
    public void createDuplicateHolidayKind() throws Exception {
        holidayKindRepository.save(holidayKind("추석", OLD_IMAGE_URL, true));

        mockMvc.perform(multipart("/admin/holiday-kinds")
                        .file(badgeImage())
                        .param("name", "추석")
                        .param("badgeEnabled", "true")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("form"));

        verify(s3Service, never()).uploadFile(any(MultipartFile.class), any(ImageCategory.class));
        assertThat(holidayKindRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("새 뱃지 이미지 없이 연휴 종류를 수정하면 기존 이미지를 유지한다.")
    public void modifyHolidayKindWithoutImage() throws Exception {
        HolidayKind kind = holidayKindRepository.save(holidayKind("추석", OLD_IMAGE_URL, true));

        mockMvc.perform(multipart("/admin/holiday-kinds/{id}/edit", kind.getId())
                        .param("name", "한가위")
                        .param("badgeEnabled", "false")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/holiday-kinds"));

        assertThat(holidayKindRepository.findById(kind.getId()))
                .get()
                .satisfies(modified -> {
                    assertThat(modified.getName()).isEqualTo("한가위");
                    assertThat(modified.isBadgeEnabled()).isFalse();
                    assertThat(modified.getBadgeImageUrl()).isEqualTo(OLD_IMAGE_URL);
                });
        verify(s3Service, never()).deleteFile(any());
    }

    @Test
    @DisplayName("새 뱃지 이미지로 연휴 종류를 수정하면 이미지를 교체하고 기존 이미지를 삭제한다.")
    public void modifyHolidayKindWithImage() throws Exception {
        HolidayKind kind = holidayKindRepository.save(holidayKind("추석", OLD_IMAGE_URL, true));

        mockMvc.perform(multipart("/admin/holiday-kinds/{id}/edit", kind.getId())
                        .file(badgeImage())
                        .param("name", "추석")
                        .param("badgeEnabled", "true")
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/holiday-kinds"));

        assertThat(holidayKindRepository.findById(kind.getId()))
                .get()
                .extracting(HolidayKind::getBadgeImageUrl)
                .isEqualTo(UPLOADED_IMAGE_URL);
        verify(s3Service).deleteFile(OLD_IMAGE_URL);
    }

    @Test
    @DisplayName("연휴가 등록되지 않은 연휴 종류를 삭제하고 뱃지 이미지를 삭제한다.")
    public void removeHolidayKind() throws Exception {
        HolidayKind kind = holidayKindRepository.save(holidayKind("추석", OLD_IMAGE_URL, true));

        mockMvc.perform(post("/admin/holiday-kinds/{id}/delete", kind.getId())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/holiday-kinds"));

        assertThat(holidayKindRepository.findAll()).isEmpty();
        verify(s3Service).deleteFile(OLD_IMAGE_URL);
    }

    @Test
    @DisplayName("연휴가 등록된 연휴 종류는 삭제하지 않는다.")
    public void removeHolidayKindInUse() throws Exception {
        HolidayKind kind = holidayKindRepository.save(holidayKind("추석", OLD_IMAGE_URL, true));
        LocalDate today = TimeUtil.currentStudyDate();
        holidayRepository.save(Holiday.builder()
                .holidayKind(kind)
                .startDate(today.plusDays(10))
                .endDate(today.plusDays(12))
                .build());

        mockMvc.perform(post("/admin/holiday-kinds/{id}/delete", kind.getId())
                        .with(user(ADMIN))
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/holiday-kinds"))
                .andExpect(flash().attributeExists("errorMessage"));

        assertThat(holidayKindRepository.findAll()).hasSize(1);
        verify(s3Service, never()).deleteFile(any());
    }

    private HolidayKind holidayKind(String name, String badgeImageUrl, boolean badgeEnabled) {
        return HolidayKind.builder().name(name).badgeImageUrl(badgeImageUrl).badgeEnabled(badgeEnabled).build();
    }

    private MockMultipartFile badgeImage() {
        return new MockMultipartFile("badgeImage", "badge.png", "image/png", new byte[]{1, 2, 3});
    }
}
