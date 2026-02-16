package com.cdy.cdy.domain.users.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Users {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username") // 유저 로그인 아이디(이메일형식)
    private String username;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "profile_image_key")
    private String profileImageKey;

    @Column(name = "password") //비밀번호
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_category") //유저 공부분야
    private UserCategory userCategory;

    @Column(name = "description") // 한줄소개
    private String description;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
