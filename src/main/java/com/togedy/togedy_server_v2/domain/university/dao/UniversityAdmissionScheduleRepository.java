package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityAdmissionScheduleRepository extends JpaRepository<UniversityAdmissionSchedule, Long> {

    /**
     * 해당 대학의 해당 학년도 전형들이 가진 일정을 조회한다.
     * <p>
     * {@code uas.universityAdmissionMethod}는 1:N 컬렉션이 아닌 다대일 연관관계이므로 FETCH JOIN해도 루트 엔티티 중복이 발생하지
     * 않는다. 반면 {@link com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod}를 루트로 하여
     * {@code universityAdmissionScheduleList}(1:N)를 FETCH JOIN하면 전형이 보유한 일정 수만큼 전형 자체가 중복되므로, 전형별 일정을
     * 조회할 때는 이처럼 일정(uas)을 루트로 조회한 뒤 서비스단에서 전형ID 기준으로 그룹핑한다.
     * </p>
     */
    @Query("""
                SELECT uas
                FROM UniversityAdmissionSchedule uas
                    JOIN FETCH uas.universitySchedule us
                    JOIN FETCH uas.universityAdmissionMethod uam
                WHERE uam.university = :university
                    AND uam.academicYear = :academicYear
            """)
    List<UniversityAdmissionSchedule> findAllByUniversityAndAcademicYear(
            @Param("university") University university,
            @Param("academicYear") int academicYear
    );

    /**
     * 유저가 담은 대학 전형의 대학 일정 중, 해당 월과 겹치는 일정을 조회한다.
     * <p>
     * {@code uam.userUniversityMethodList}는 조회 결과에 포함할 필요가 없으므로(유저 소유 여부 필터링에만 사용) FETCH가 아닌 일반 JOIN을
     * 사용한다. 1:N 컬렉션을 FETCH JOIN하지 않아 루트 엔티티 중복 및 컬렉션 부분 초기화 문제를 방지한다.
     * </p>
     */
    @Query("""
                SELECT uas
                FROM UniversityAdmissionSchedule uas
                    JOIN FETCH uas.universitySchedule us
                    JOIN FETCH uas.universityAdmissionMethod uam
                    JOIN FETCH uam.university u
                    JOIN uam.userUniversityMethodList uum
                WHERE uum.user.id = :userId
                    AND us.startDate <= :endOfMonth
                    AND COALESCE(us.endDate, us.startDate) >= :startOfMonth
            """)
    List<UniversityAdmissionSchedule> findByUserIdAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("endOfMonth") LocalDate endOfMonth
    );

    /**
     * 유저가 담은 대학 전형의 대학 일정 중, 해당 날짜와 겹치는 일정을 조회한다.
     * <p>
     * {@code uam.userUniversityMethodList}는 조회 결과에 포함할 필요가 없으므로(유저 소유 여부 필터링에만 사용) FETCH가 아닌 일반 JOIN을
     * 사용한다. 1:N 컬렉션을 FETCH JOIN하지 않아 루트 엔티티 중복 및 컬렉션 부분 초기화 문제를 방지한다.
     * </p>
     */
    @Query("""
                SELECT uas
                FROM UniversityAdmissionSchedule uas
                    JOIN FETCH uas.universitySchedule us
                    JOIN FETCH uas.universityAdmissionMethod uam
                    JOIN FETCH uam.university u
                    JOIN uam.userUniversityMethodList uum
                WHERE uum.user.id = :userId
                    AND :date BETWEEN us.startDate AND COALESCE(us.endDate, us.startDate)
            """)
    List<UniversityAdmissionSchedule> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );
}
