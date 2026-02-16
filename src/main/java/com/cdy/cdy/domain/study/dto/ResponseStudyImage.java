package com.cdy.cdy.domain.study.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseStudyImage {


    private Long id;
    private String imageUrl;
    private Integer sortOrder;
}
