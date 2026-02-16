package com.cdy.cdy.domain.study.repository;

import com.cdy.cdy.domain.study.entity.Study;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study,Long> {


    @Query("""
            SELECT COUNT(s)
            FROM Study s
            WHERE s.userId = :userId
            AND s.isDeleted = FALSE
            """)
    Long findTotalCountByUserId(@Param("userId") Long userId);

}
