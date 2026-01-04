package com.togedy.togedy_server_v2.domain.user.entity;

import com.togedy.togedy_server_v2.domain.user.enums.ProviderType;
import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "auth_provider",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "provider_user_id"}),
                @UniqueConstraint(columnNames = {"provider", "user_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthProvider extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_provider_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderType provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "profile_completed", nullable = false)
    private boolean profileCompleted;

    private AuthProvider(User user, ProviderType provider, String providerUserId, String email, boolean profileCompleted) {
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.email = email;
        this.profileCompleted = profileCompleted;
    }

    public static AuthProvider kakao(User user, String kakaoId, String email) {
        return new AuthProvider(user, ProviderType.KAKAO, kakaoId, email, false);
    }

    public static AuthProvider local(User user, String email) {
        return new AuthProvider(user, ProviderType.LOCAL, email, email, true);
    }

    public void completeProfile() {
        this.profileCompleted = true;
    }

}
