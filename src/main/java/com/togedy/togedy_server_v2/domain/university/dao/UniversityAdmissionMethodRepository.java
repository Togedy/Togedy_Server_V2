package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.dto.UniversityAdmissionMethodCountRow;
import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UniversityAdmissionMethodRepository extends JpaRepository<UniversityAdmissionMethod, Long> {

    @Query("""
                SELECT uam
                FROM UniversityAdmissionMethod uam
                    JOIN FETCH uam.userUniversityMethodList uum
                WHERE uum.user.id = :userId
                    AND uam.university = :university
                    AND uam.academicYear = :academicYear
            """)
    List<UniversityAdmissionMethod> findAllByUniversityAndUserIdAndAcademicYear(
            @Param("university") University university,
            @Param("userId") Long userId,
            @Param("academicYear") int academicYear
    );

    @Query("""
                SELECT uam
                FROM UniversityAdmissionMethod uam
                    JOIN FETCH uam.userUniversityMethodList uum
                WHERE uum.user.id = :userId
                    AND uam.university.id IN :universityIds
                    AND uam.academicYear = :academicYear
            """)
    List<UniversityAdmissionMethod> findAllByUniversityIdsAndUserIdAndAcademicYear(
            @Param("universityIds") List<Long> universityIds,
            @Param("userId") Long userId,
            @Param("academicYear") int academicYear
    );

    /**
     * 해당 대학의 해당 학년도 전형 목록을 조회한다.
     * <p>
     * 전형이 보유한 일정({@code universityAdmissionScheduleList})은 1:N 컬렉션이므로 FETCH JOIN하지 않는다. FETCH JOIN할 경우
     * 전형이 가진 일정 수만큼 전형 자체가 중복되어 반환되기 때문이며, 일정은
     * {@link UniversityAdmissionScheduleRepository#findAllByUniversityAndAcademicYear}로 별도 조회하여 서비스단에서
     * 전형ID 기준으로 그룹핑한다.
     * </p>
     */
    @Query("""
                SELECT uam
                FROM UniversityAdmissionMethod uam
                WHERE uam.university = :university
                    AND uam.academicYear = :academicYear
            """)
    List<UniversityAdmissionMethod> findAllByUniversityAndAcademicYear(
            @Param("university") University university,
            @Param("academicYear") int academicYear
    );


    @Query("""
                SELECT new com.togedy.togedy_server_v2.domain.university.dto.UniversityAdmissionMethodCountRow(u.id, COUNT(uam))
                FROM UniversityAdmissionMethod uam
                    JOIN uam.university u
                WHERE u.id IN :universityIds
                    AND uam.academicYear = :academicYear
                GROUP BY u.id
            """)
    List<UniversityAdmissionMethodCountRow> findCountByUniversityIdsAndAcademicYear(
            @Param("universityIds") List<Long> universityIds,
            @Param("academicYear") int academicYear
    );
}
