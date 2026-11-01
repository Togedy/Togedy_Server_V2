package com.togedy.togedy_server_v2.domain.badge.dao;

import com.togedy.togedy_server_v2.domain.badge.entity.Badge;
import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
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

    Optional<Badge> findByIdAndUserId(Long id, Long userId);
}
