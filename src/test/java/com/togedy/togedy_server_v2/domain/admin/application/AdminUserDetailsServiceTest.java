package com.togedy.togedy_server_v2.domain.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.togedy.togedy_server_v2.domain.admin.dao.AdminAccountRepository;
import com.togedy.togedy_server_v2.domain.admin.entity.AdminAccount;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.enums.UserRole;
import com.togedy.togedy_server_v2.domain.user.enums.UserStatus;
import com.togedy.togedy_server_v2.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.global.security.AdminUser;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AdminUserDetailsServiceTest {

    private static final String LOGIN_ID = "admin";
    private static final Long USER_ID = 1L;

    @Mock
    private AdminAccountRepository adminAccountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserDetailsService adminUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFixture.createUser();
        ReflectionTestUtils.setField(user, "id", USER_ID);
    }

    @Test
    void 어드민_계정이_없으면_예외가_발생한다() {
        given(adminAccountRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserDetailsService.loadUserByUsername(LOGIN_ID))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void 연결된_유저의_권한이_ADMIN이_아니면_예외가_발생한다() {
        givenAdminAccount();

        assertThatThrownBy(() -> adminUserDetailsService.loadUserByUsername(LOGIN_ID))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void 연결된_유저가_비활성_상태면_예외가_발생한다() {
        givenAdminAccount();
        ReflectionTestUtils.setField(user, "userRole", UserRole.ADMIN);
        user.updateStatus(UserStatus.INACTIVE);

        assertThatThrownBy(() -> adminUserDetailsService.loadUserByUsername(LOGIN_ID))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void 활성_상태의_ADMIN_유저면_어드민_principal을_반환한다() {
        givenAdminAccount();
        ReflectionTestUtils.setField(user, "userRole", UserRole.ADMIN);

        UserDetails userDetails = adminUserDetailsService.loadUserByUsername(LOGIN_ID);

        assertThat(userDetails).isInstanceOf(AdminUser.class);
        assertThat(((AdminUser) userDetails).getId()).isEqualTo(USER_ID);
        assertThat(userDetails.getUsername()).isEqualTo(LOGIN_ID);
        assertThat(userDetails.getPassword()).isEqualTo("encoded");
        assertThat(userDetails.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_ADMIN");
    }

    private void givenAdminAccount() {
        AdminAccount adminAccount = AdminAccount.builder()
                .loginId(LOGIN_ID)
                .password("encoded")
                .userId(USER_ID)
                .build();

        given(adminAccountRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(adminAccount));
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
    }
}
