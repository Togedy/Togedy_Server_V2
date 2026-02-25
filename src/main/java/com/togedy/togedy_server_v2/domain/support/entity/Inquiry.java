package com.togedy.togedy_server_v2.domain.support.entity;

import com.togedy.togedy_server_v2.domain.support.enums.InquiryStatus;
import com.togedy.togedy_server_v2.domain.support.enums.InquiryType;
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
@Table(name = "inquiry")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "type", columnDefinition = "varchar(20)", nullable = false)
    private InquiryType type;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "reply_email", nullable = false)
    private String replyEmail;

    @Column(name = "status", columnDefinition = "varchar(20)", nullable = false)
    private InquiryStatus status;

    @Builder
    public Inquiry(Long userId, InquiryType inquiryType, String content, String replyEmail) {
        this.userId = userId;
        this.type = inquiryType;
        this.content = content;
        this.replyEmail = replyEmail;
        this.status = InquiryStatus.WAITING;
    }
}
