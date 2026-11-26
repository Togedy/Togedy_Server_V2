package com.togedy.togedy_server_v2.domain.badge.dao;

import com.togedy.togedy_server_v2.domain.badge.entity.Badge;
import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    @Query("""
            SELECT b.type
            FROM Badge b
            WHERE b.userId = :userId
                AND b.type IN :types
            """)
    Set<BadgeType> findTypesByUserIdAndTypeIn(Long userId, Collection<BadgeType> types);

    @Query("""
            SELECT b.userId
            FROM Badge b
            WHERE b.type = :type
                AND b.periodKey = :periodKey
            """)
    Set<Long> findUserIdsByTypeAndPeriodKey(BadgeType type, String periodKey);

    boolean existsByUserIdAndTypeAndPeriodKey(Long userId, BadgeType type, String periodKey);

    List<Badge> findAllByUserIdOrderByCreatedAtDescIdDesc(Long userId);

    List<Badge> findAllByUserIdAndType(Long userId, BadgeType type);

    // 동시 요청이 같은 미확인 뱃지를 반환하지 않도록 확인 처리가 커밋될 때까지 잠근다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Badge> findAllByUserIdAndCheckedFalseOrderByCreatedAtAscIdAsc(Long userId);

    Optional<Badge> findByIdAndUserId(Long id, Long userId);

    void deleteAllByUserId(Long userId);
}
