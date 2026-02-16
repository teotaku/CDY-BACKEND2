package com.cdy.cdy.domain.study.repository;

import com.cdy.cdy.domain.study.entity.StudyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyImageRepository extends JpaRepository<StudyImage,Long> {


    @Modifying
            @Query("""
                    DELETE StudyImage si
                    WHERE si.studyId = :studyId
                    """)
    void deleteAllByStudyId(@Param("studyId") Long studyId);


    @Query("""
            SELECT si
            FROM StudyImage si
            WHERE si.studyId = :studyId
            """)
    List<StudyImage> findByStudyId(@Param("studyId") Long studyId);

}
