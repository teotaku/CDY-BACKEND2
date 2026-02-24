package com.cdy.cdy.domain.study.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 스터디 수정 DTO
 */
@Getter
@Setter
public class RequestStudy {
    //스터디 글 제목
    private String title;
    //스터디 글 내용
    private String content;
    //스터디 이미지 리스트
    private List<RequestStudyImage> imageList = new ArrayList<>();

}
