package com.cdy.cdy.domain.study.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 스터디 단건 조회 DTO
 */
@Getter
@Setter
public class RequestStudy {

    private String title;

    private String content;

    private List<RequestStudyImage> imageList = new ArrayList<>();

}
