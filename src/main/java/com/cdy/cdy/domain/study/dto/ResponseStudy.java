package com.cdy.cdy.domain.study.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStudy {


    private Long id;
    private String title;
    private String content;
    private List<ResponseStudyImage> studyImageList;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
