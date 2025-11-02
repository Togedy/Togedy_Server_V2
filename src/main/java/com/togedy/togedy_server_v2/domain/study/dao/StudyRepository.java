package com.togedy.togedy_server_v2.domain.study.dao;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.enums.StudyTag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    boolean existsByName(String studyName);

    @Query("""
            SELECT s
            FROM Study s
            JOIN UserStudy us ON us.studyId = s.id
            WHERE us.userId = :userId
            ORDER BY us.createdAt ASC
            """)
    List<Study> findAllByUserIdOrderByCreatedAtAsc(Long userId);

    @Query("""
                SELECT s
                FROM Study s
                WHERE s.tag IN :studyTags
                AND (:joinable = false OR s.memberCount < s.memberLimit)
                AND (:challenge = false OR s.type = 'CHALLENGE')
                AND (:name IS NULL OR s.name LIKE %:name%)
                ORDER BY
                    CASE WHEN :filter = 'latest' THEN s.createdAt END DESC,
                    CASE WHEN :filter = 'most' THEN s.memberCount END DESC,
                    CASE WHEN :filter = 'least' THEN s.memberCount END ASC
            """)
    Slice<Study> findStudiesWithTags(
            @Param("name") String name,
            @Param("studyTags") List<StudyTag> studyTags,
            @Param("filter") String filter,
            @Param("joinable") boolean joinable,
            @Param("challenge") boolean challenge,
            Pageable pageable
    );

    @Query("""
                SELECT s
                FROM Study s
                WHERE (:joinable = false OR s.memberCount < s.memberLimit)
                AND (:challenge = false OR s.type = 'CHALLENGE')
                AND (:name IS NULL OR s.name LIKE %:name%)
                ORDER BY
                    CASE WHEN :filter = 'latest' THEN s.createdAt END DESC,
                    CASE WHEN :filter = 'most' THEN s.memberCount END DESC,
                    CASE WHEN :filter = 'least' THEN s.memberCount END ASC
            """)
    Slice<Study> findStudiesWithoutTags(
            @Param("name") String name,
            @Param("filter") String filter,
            @Param("joinable") boolean joinable,
            @Param("challenge") boolean challenge,
            Pageable pageable
    );

    @Query("""
                SELECT s
                FROM Study s
                JOIN UserStudy us ON s.id = us.studyId
                JOIN User u ON us.userId = u.id
                WHERE u.status = 'STUDYING'
                GROUP BY s.id
                ORDER BY COUNT(u.id) DESC
            """)
    List<Study> findMostAcitveStudies(Pageable pageable);
}
