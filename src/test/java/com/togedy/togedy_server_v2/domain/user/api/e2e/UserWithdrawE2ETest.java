package com.togedy.togedy_server_v2.domain.user.api.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.togedy.togedy_server_v2.domain.badge.dao.BadgeRepository;
import com.togedy.togedy_server_v2.domain.badge.entity.Badge;
import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import com.togedy.togedy_server_v2.domain.chat.dao.ChatMessageRepository;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Tag("E2E 테스트")
public class UserWithdrawE2ETest extends AbstractE2ETest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BadgeRepository badgeRepository;

    @MockitoBean
    ChatMessageRepository chatMessageRepository;

    @Test
    @DisplayName("회원 탈퇴 시 탈퇴한 유저의 뱃지만 삭제한다.")
    public void withdrawUserDeletesBadges() throws Exception {
        User user = fixtureSupport.persistUser(UserFixture.createUser());
        User other = fixtureSupport.persistUser(UserFixture.createUserWithName("다른유저"));
        String accessToken = testJwtFactory.createAccessToken(user.getId());
        saveBadge(user, BadgeType.COMEBACK);
        saveBadge(user, BadgeType.ONE_SUBJECT_30);
        Badge otherBadge = saveBadge(other, BadgeType.COMEBACK);

        mockMvc.perform(delete("/api/v2/users/me")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk());

        assertThat(userRepository.findById(user.getId())).isEmpty();
        assertThat(badgeRepository.findAll())
                .extracting(Badge::getId)
                .containsExactly(otherBadge.getId());
    }

    private Badge saveBadge(User user, BadgeType type) {
        return badgeRepository.save(Badge.builder()
                .userId(user.getId())
                .type(type)
                .date(LocalDate.of(2026, 10, 1))
                .build());
    }
}
