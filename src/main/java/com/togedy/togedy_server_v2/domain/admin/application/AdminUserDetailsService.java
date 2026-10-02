package com.togedy.togedy_server_v2.domain.admin.application;

import com.togedy.togedy_server_v2.domain.admin.dao.AdminAccountRepository;
import com.togedy.togedy_server_v2.domain.admin.entity.AdminAccount;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.enums.UserRole;
import com.togedy.togedy_server_v2.domain.user.enums.UserStatus;
import com.togedy.togedy_server_v2.global.security.AdminUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminAccountRepository adminAccountRepository;
    private final UserRepository userRepository;

    /**
     * 어드민 로그인 ID로 계정을 조회한다.
     * <p>
     * 연결된 유저의 권한이 ADMIN이고 활성 상태인 경우에만 로그인을 허용한다. 실패 사유는 외부에 노출하지 않도록 모두 동일한 예외로 처리한다.
     * </p>
     *
     * @param loginId 어드민 로그인 ID
     * @return 어드민 principal
     * @throws UsernameNotFoundException 계정이 없거나, 유저가 ADMIN이 아니거나, 비활성 상태인 경우
     */
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        AdminAccount adminAccount = adminAccountRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException(loginId));

        User user = userRepository.findById(adminAccount.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException(loginId));

        if (user.getUserRole() != UserRole.ADMIN || user.getStatus() != UserStatus.ACTIVE) {
            throw new UsernameNotFoundException(loginId);
        }

        return AdminUser.builder()
                .id(user.getId())
                .loginId(adminAccount.getLoginId())
                .password(adminAccount.getPassword())
                .build();
    }
}
