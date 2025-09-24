package com.togedy.togedy_server_v2.domain.user.entity;

import com.togedy.togedy_server_v2.domain.user.enums.UserStatus;
import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    @Column(name = "nickname", length = 10, nullable = false, unique = true)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image_url", nullable = true)
    private String profileImageUrl;

    @Column(name = "profile_message", nullable = true)
    private String profileMessage;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "study_streak", nullable = false)
    private int studyStreak;

    @Column(name = "last_activated_at", nullable = true)
    private LocalDateTime lastActivatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    private User(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
        this.status = UserStatus.ACTIVE;
    }

    public static User create(String nickname, String email) {
        return new User(nickname, email);
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
    }
}
