package com.togedy.togedy_server_v2.domain.badge.application;

import com.togedy.togedy_server_v2.domain.badge.dao.BadgeRepository;
import com.togedy.togedy_server_v2.domain.badge.dto.BadgeItemResponse;
import com.togedy.togedy_server_v2.domain.badge.dto.GetBadgeResponse;
import com.togedy.togedy_server_v2.domain.badge.dto.GetMyBadgesResponse;
import com.togedy.togedy_server_v2.domain.badge.dto.RepresentativeBadgeResponse;
import com.togedy.togedy_server_v2.domain.badge.dto.YearlyBadgeResponse;
import com.togedy.togedy_server_v2.domain.badge.entity.Badge;
import com.togedy.togedy_server_v2.domain.badge.exception.BadgeNotFoundException;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.user.UserNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;

    /**
     * 본인의 대표 뱃지와 연도별 보유 뱃지를 조회한다.
     * <p>
     * 연도는 받은 날짜 기준 최신순이며, 연도 안에서는 표시 이름이 같은 뱃지를 하나로 묶어 받은 횟수를 담는다. 묶음은 가장 최근에 받은 순으로 정렬하고, 묶음의 badgeId는 가장 최근에 받은 뱃지의
     * ID이다. 대표 뱃지를 설정하지 않았거나 보유하지 않은 뱃지를 가리키면 대표 뱃지는 null이다.
     * </p>
     *
     * @param userId 유저 ID
     * @return 대표 뱃지와 연도별 보유 뱃지
     */
    @Transactional(readOnly = true)
    public GetMyBadgesResponse findMyBadges(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<Badge> badges = badgeRepository.findAllByUserIdOrderByCreatedAtDescIdDesc(userId);

        RepresentativeBadgeResponse representativeBadge = badges.stream()
                .filter(badge -> badge.getId().equals(user.getRepresentativeBadgeId()))
                .findFirst()
                .map(badge -> RepresentativeBadgeResponse.of(
                        badge.getId(),
                        badge.getDisplayName(),
                        badge.getType().getImageUrl(),
                        badge.getAcquiredDate()
                ))
                .orElse(null);

        return GetMyBadgesResponse.of(representativeBadge, groupByYear(badges));
    }

    /**
     * 본인이 보유한 뱃지의 상세 정보를 조회한다.
     *
     * @param badgeId 뱃지 ID
     * @param userId  유저 ID
     * @return 뱃지 상세 정보
     */
    @Transactional(readOnly = true)
    public GetBadgeResponse findMyBadge(Long badgeId, Long userId) {
        Badge badge = badgeRepository.findByIdAndUserId(badgeId, userId)
                .orElseThrow(BadgeNotFoundException::new);

        return GetBadgeResponse.of(
                badge.getId(),
                badge.getDisplayName(),
                badge.getType().getImageUrl(),
                badge.getType().getDescription()
        );
    }

    /**
     * 받은 날짜 최신순으로 정렬된 뱃지를 연도별, 표시 이름별로 묶는다.
     *
     * @param badges 받은 날짜 최신순으로 정렬된 뱃지 목록
     * @return 연도 최신순의 연도별 뱃지 묶음
     */
    private List<YearlyBadgeResponse> groupByYear(List<Badge> badges) {
        Map<Integer, List<Badge>> badgesByYear = badges.stream()
                .collect(Collectors.groupingBy(
                        badge -> badge.getAcquiredDate().getYear(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return badgesByYear.entrySet().stream()
                .map(entry -> YearlyBadgeResponse.of(entry.getKey(), groupByDisplayName(entry.getValue())))
                .toList();
    }

    /**
     * 같은 연도의 뱃지를 표시 이름별로 묶고, 가장 최근에 받은 뱃지를 대표로 삼는다.
     *
     * @param badges 같은 연도에 받은 뱃지 목록, 받은 날짜 최신순
     * @return 가장 최근에 받은 순의 뱃지 묶음
     */
    private List<BadgeItemResponse> groupByDisplayName(List<Badge> badges) {
        Map<String, List<Badge>> badgesByName = badges.stream()
                .collect(Collectors.groupingBy(
                        Badge::getDisplayName,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return badgesByName.values().stream()
                .map(group -> {
                    Badge latest = group.get(0);
                    return BadgeItemResponse.of(
                            latest.getId(),
                            latest.getDisplayName(),
                            latest.getType().getImageUrl(),
                            group.size()
                    );
                })
                .toList();
    }
}
