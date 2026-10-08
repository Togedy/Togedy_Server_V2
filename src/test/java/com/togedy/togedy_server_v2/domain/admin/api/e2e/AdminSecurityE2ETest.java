package com.togedy.togedy_server_v2.domain.admin.api.e2e;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.togedy.togedy_server_v2.domain.admin.dao.AdminAccountRepository;
import com.togedy.togedy_server_v2.domain.admin.entity.AdminAccount;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.enums.UserRole;
import com.togedy.togedy_server_v2.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.global.security.AdminUser;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@Tag("E2E 테스트")
public class AdminSecurityE2ETest extends AbstractE2ETest {

    private static final String LOGIN_ID = "admin";
    private static final String PASSWORD = "password";

    @Autowired
    AdminAccountRepository adminAccountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인하지 않으면 어드민 페이지 접근 시 로그인 페이지로 이동한다.")
    public void redirectToLoginPage() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/admin/login"));
    }

    @Test
    @DisplayName("로그인 페이지는 로그인 없이 조회할 수 있다.")
    public void readLoginPage() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN 권한 유저의 어드민 계정으로 로그인한다.")
    public void loginWithAdminAccount() throws Exception {
        saveAdminAccount(UserRole.ADMIN);

        mockMvc.perform(formLogin("/admin/login").userParameter("loginId").user(LOGIN_ID).password(PASSWORD))
                .andExpect(redirectedUrl("/admin"))
                .andExpect(authenticated().withUsername(LOGIN_ID).withRoles("ADMIN"));
    }

    @Test
    @DisplayName("비밀번호가 틀리면 로그인에 실패한다.")
    public void loginWithWrongPassword() throws Exception {
        saveAdminAccount(UserRole.ADMIN);

        mockMvc.perform(formLogin("/admin/login").userParameter("loginId").user(LOGIN_ID).password("wrong"))
                .andExpect(redirectedUrl("/admin/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("연결된 유저의 권한이 ADMIN이 아니면 로그인에 실패한다.")
    public void loginWithUserRole() throws Exception {
        saveAdminAccount(UserRole.USER);

        mockMvc.perform(formLogin("/admin/login").userParameter("loginId").user(LOGIN_ID).password(PASSWORD))
                .andExpect(redirectedUrl("/admin/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("로그인한 관리자는 어드민 메인 페이지를 조회한다.")
    public void readMainPage() throws Exception {
        mockMvc.perform(get("/admin").with(user(admin())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CSRF 토큰 없이 POST 요청을 보내면 거부된다.")
    public void rejectPostWithoutCsrf() throws Exception {
        mockMvc.perform(post("/admin/notices")
                        .param("noticeTitle", "제목")
                        .param("noticeContent", "내용")
                        .with(user(admin())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그아웃하면 로그인 페이지로 이동한다.")
    public void logoutAdmin() throws Exception {
        mockMvc.perform(post("/admin/logout").with(user(admin())).with(csrf()))
                .andExpect(redirectedUrl("/admin/login?logout"));
    }

    private void saveAdminAccount(UserRole userRole) {
        User user = UserFixture.createUser();
        ReflectionTestUtils.setField(user, "userRole", userRole);
        fixtureSupport.persistUser(user);

        adminAccountRepository.save(AdminAccount.builder()
                .loginId(LOGIN_ID)
                .password(passwordEncoder.encode(PASSWORD))
                .userId(user.getId())
                .build());
    }

    private AdminUser admin() {
        return AdminUser.builder().id(1L).loginId(LOGIN_ID).build();
    }
}
