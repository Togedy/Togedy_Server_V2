package com.togedy.togedy_server_v2.domain.university.entity;

import com.togedy.togedy_server_v2.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_university_schedule",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "university_schedule_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUniversitySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_university_schedule_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_schedule_id", nullable = false)
    private UniversitySchedule universitySchedule;

    @Builder
    public UserUniversitySchedule(User user, UniversitySchedule universitySchedule) {
        this.user = user;
        this.universitySchedule = universitySchedule;
    }
}
