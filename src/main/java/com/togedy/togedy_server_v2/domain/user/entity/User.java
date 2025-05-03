package com.togedy.togedy_server_v2.domain.user.entity;

import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "status")
    private String status;

    @Builder
    public User(String nickname, String profileImageUrl, String status) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
    }
}
