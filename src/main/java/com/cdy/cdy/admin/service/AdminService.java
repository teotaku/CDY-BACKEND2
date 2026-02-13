package com.cdy.cdy.admin.service;

import com.cdy.cdy.admin.dto.UserRequestDto;
import com.cdy.cdy.domain.users.entity.Users;
import com.cdy.cdy.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {


    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    //관리자가 회원가입시키는 로직
    public void join(UserRequestDto userRequestDto) {


        userRepository.findByUsername(userRequestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("이미 존재하는 아이디입니다."));

        Users users = Users.builder()
                .nickname(userRequestDto.getNickname())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .description(userRequestDto.getDescription())
                .username(userRequestDto.getUsername())
                .build();

        userRepository.save(users);

    }
}
