package com.cdy.cdy.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    public interface existGroup {}
    public interface addGroup{}
    public interface passwordGroup {}
    public interface updateGroup {}
    public interface deleteGroup {}

    @NotBlank(groups = {existGroup.class, addGroup.class, updateGroup.class, deleteGroup.class}) @Size(min = 2)
    //로그인 아이디 (이메일)
    private String username;
    @NotBlank(groups = {addGroup.class, passwordGroup.class}) @Size(min = 4)
    //비밀번호
    private String password;
    @NotBlank(groups = {addGroup.class, updateGroup.class})
    //닉네임
    @NotBlank(groups = {addGroup.class, updateGroup.class})
    private String nickname;
    //자기소개 한글
    private String description;

}
