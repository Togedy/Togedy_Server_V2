package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.University;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UniversityRepository extends JpaRepository<University, Long> {

    @Query("""
        SELECT u
        FROM University u
        WHERE u.name LIKE CONCAT('%', :name, '%')
            AND (:admissionType IS NULL OR u.admissionType = :admissionType)
    """)
    Slice<University> findByNameAndType(
            @Param("name")  String name,
            @Param("admissionType") String admissionType,
            Pageable pageable
    );
}
