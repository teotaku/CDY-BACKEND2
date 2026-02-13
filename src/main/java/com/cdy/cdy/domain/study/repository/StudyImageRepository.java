package com.cdy.cdy.domain.study.repository;

import com.cdy.cdy.domain.study.entity.StudyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyImageRepository extends JpaRepository<StudyImage,Long> {
}
