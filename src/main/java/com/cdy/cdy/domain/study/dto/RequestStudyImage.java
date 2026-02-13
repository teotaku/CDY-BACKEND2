package com.cdy.cdy.domain.study.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestStudyImage {

    // presign 이미지키
    private String imageKey;
    //노출 순서
    private Integer sortOrder;


}
