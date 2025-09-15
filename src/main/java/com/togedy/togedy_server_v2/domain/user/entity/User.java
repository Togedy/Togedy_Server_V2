package com.togedy.togedy_server_v2.domain.user.entity;

import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import com.togedy.togedy_server_v2.global.enums.BaseStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BaseStatus status;

    private User(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
        this.status = BaseStatus.ACTIVE;
    }

    public static User create(String nickname, String email) {
        return new User(nickname, email);
    }

    public void updateStatus(BaseStatus status) {
        this.status = status;
    }
}
