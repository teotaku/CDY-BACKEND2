package com.cdy.cdy.domain.study.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 스터디 단건 조회 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStudy {

    @Schema(description = "스터디 고유 아이디")
    private Long id;
    @Schema(description = "스터디 제목")
    private String title;
    @Schema(description = "스터디 내용")
    private String content;
    @Schema(description = "스터디 이미지 목록")
    private List<ResponseStudyImage> studyImageList;
    @Schema(description = "작성날짜")
    private LocalDateTime createdAt;
    @Schema(description = "수정날짜")
    private LocalDateTime updatedAt;
}
