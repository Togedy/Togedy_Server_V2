package com.togedy.togedy_server_v2.domain.user.entity;

import com.togedy.togedy_server_v2.domain.user.enums.ProviderType;
import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

    private AuthProvider(User user, ProviderType provider, String providerUserId) {
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
    }

    public static AuthProvider kakao(User user, String kakaoId) {
        return new AuthProvider(user, ProviderType.KAKAO, kakaoId);
    }

    public static AuthProvider local(User user) {
        return new AuthProvider(user, ProviderType.LOCAL, user.getEmail());
    }
}
