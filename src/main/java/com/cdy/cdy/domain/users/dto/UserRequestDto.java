package com.cdy.cdy.domain.users.dto;

import com.cdy.cdy.domain.users.entity.UserCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    public interface loginGroup {}
    public interface existGroup {}
    public interface addGroup{}
    public interface passwordGroup {}
    public interface updateGroup {}
    public interface deleteGroup {}

    @NotBlank(groups = {loginGroup.class, existGroup.class, addGroup.class, deleteGroup.class}) @Size(min = 2)
    //로그인 아이디 (이메일)
    @Schema(example = "codiyoung@codiyoung.com")
    private String username;
    @NotBlank(groups = {loginGroup.class, addGroup.class, passwordGroup.class}) @Size(min = 4)
    //비밀번호
    @Schema(example = "123456")
    private String password;
    @NotBlank(groups = {addGroup.class, updateGroup.class})
    //닉네임
    @NotBlank(groups = {addGroup.class, updateGroup.class})
    @Schema(example = "test")
    private String nickname;
    //유저 카테고리 ex) 영상편집,코딩,디자인
    @NotNull(groups = {addGroup.class, updateGroup.class})
    private UserCategory userCategory;
    //자기소개 한글
    private String description;

    @NotBlank(groups = {addGroup.class})
    //유저의 본명
    @Schema(example = "홍길동", description = "유저의 본명")
    private String name;

    @NotBlank(groups = {addGroup.class})
    @Schema(example = "01012345678", description = "휴대폰 번호")
    //휴대폰 번호
    private String phoneNumber;
}
