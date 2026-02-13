package com.cdy.cdy.domain.study.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "study_image")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class StudyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "study_id")
    private Long studyId;

    @Column(name = "imagekey")
    private String imageKey;

    @Column(name = "sort_order")
    private Integer sortOrder;

}
