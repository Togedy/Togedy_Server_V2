package com.togedy.togedy_server_v2.domain.config.entity;

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
@Getter
@Table(name = "calendar_announcement")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarAnnouncement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_announcement_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = true)
    private Long userId;

    @Column(name = "content", nullable = false)
    private String content;

    @Builder
    public CalendarAnnouncement(Long userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
    }
}
