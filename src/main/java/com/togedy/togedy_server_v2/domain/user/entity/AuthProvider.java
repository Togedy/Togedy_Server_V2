package com.togedy.togedy_server_v2.domain.user.entity;

import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "auth_provider",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "provider_user_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthProvider extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_provider_id", updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "profile_completed", nullable = false)
    private boolean profileCompleted;

    public static AuthProvider kakao(Long userId, String kakaoId, String email) {
        AuthProvider authProvider = new AuthProvider();
        authProvider.userId = userId;
        authProvider.provider = "KAKAO";
        authProvider.providerUserId = kakaoId;
        authProvider.email = email;
        authProvider.profileCompleted = false;
        return authProvider;
    }

    public void completeProfile() {
        this.profileCompleted = true;
    }

}
