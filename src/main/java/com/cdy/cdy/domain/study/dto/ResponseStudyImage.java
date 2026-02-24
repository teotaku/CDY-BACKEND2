package com.cdy.cdy.domain.study.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseStudyImage {

    @Schema(description = "고유 아이디 값")
    private Long id;
    @Schema(description = "이미지 url")
    private String imageUrl;
    @Schema(description = "노출순서")
    private Integer sortOrder;
}
