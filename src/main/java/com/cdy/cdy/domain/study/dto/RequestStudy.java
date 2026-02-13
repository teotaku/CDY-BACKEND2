package com.cdy.cdy.domain.study.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestStudy {

    private String title;

    private String content;

    private List<RequestStudyImage> imageList;
}
