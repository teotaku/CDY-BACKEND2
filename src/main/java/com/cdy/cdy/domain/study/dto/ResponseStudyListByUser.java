package com.cdy.cdy.domain.study.dto;


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

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String userProfileImageUrl;
    private String firstImageUrl;


    public void setUserProfileImageUrl(String imageUrl) {
        this.userProfileImageUrl = imageUrl;
    }

    public void setFirstImageUrl(String imageUrl) {
        this.firstImageUrl = imageUrl;
    }
}
