package com.cdy.cdy.domain.auth;

import com.cdy.cdy.admin.dto.UserRequestDto;
import com.cdy.cdy.domain.users.entity.Users;
import com.cdy.cdy.domain.users.repository.UserRepository;
import com.cdy.cdy.domain.users.service.UserService;
import com.cdy.cdy.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("!prod")
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public String login(UserRequestDto userRequestDto) {


        Users users = userRepository.findByUsername(userRequestDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디"));

        if (!bCryptPasswordEncoder.matches(userRequestDto.getPassword(), users.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        String jwt = jwtUtil.createJWT(users.getUsername(), users.getRole().toString(), true);

        return jwt;


    }

}
