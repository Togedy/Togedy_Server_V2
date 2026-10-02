package com.togedy.togedy_server_v2.domain.admin.dao;

import com.togedy.togedy_server_v2.domain.admin.entity.AdminAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAccountRepository extends JpaRepository<AdminAccount, Long> {

    Optional<AdminAccount> findByLoginId(String loginId);
}
