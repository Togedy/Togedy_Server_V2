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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_university_method",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "university_schedule_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUniversityMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_university_method_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_admission_method_id", nullable = false)
    private UniversityAdmissionMethod universityAdmissionMethod;

    @Builder
    public UserUniversitySchedule(User user, UniversityAdmissionMethod universityAdmissionMethod) {
        this.user = user;
        this.universityAdmissionMethod = universityAdmissionMethod;
    }
}
