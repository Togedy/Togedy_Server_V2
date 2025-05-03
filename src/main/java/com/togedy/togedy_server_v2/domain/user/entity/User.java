package com.togedy.togedy_server_v2.domain.user.entity;

import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import com.togedy.togedy_server_v2.global.enums.BaseStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    @Column(name = "nickname", length = 10, nullable = false)
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BaseStatus status;

}
