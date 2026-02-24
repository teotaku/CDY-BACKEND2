package com.cdy.cdy.domain.study.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 유저의 전체 스터디목록 조회용 DTO
 * 유저의 프로필사진, 작성날짜, 제목 , 내용, 스터디의 첫번째노출 이미지
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStudyListByUser {

    @Schema(description = "식별키")
    private Long id;
    @Schema(description = "스터디제목")
    private String title;
    @Schema(description = "스터디 내용")
    private String content;
    @Schema(description = "작성날짜")
    private LocalDateTime createdAt;
    @Schema(description = "작성자 프로필이미지 url")
    private String userProfileImageUrl;
    @Schema(description = "첫번째 이미지 url")
    private String firstImageUrl;


    public void setUserProfileImageUrl(String imageUrl) {
        this.userProfileImageUrl = imageUrl;
    }

    public void setFirstImageUrl(String imageUrl) {
        this.firstImageUrl = imageUrl;
    }
}
