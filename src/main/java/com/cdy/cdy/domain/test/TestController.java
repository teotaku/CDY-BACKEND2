package com.cdy.cdy.domain.test;

import com.cdy.cdy.domain.users.entity.UserCategory;
import com.cdy.cdy.domain.users.entity.UserRole;
import com.cdy.cdy.domain.users.entity.Users;
import com.cdy.cdy.domain.users.repository.UserRepository;
import com.cdy.cdy.domain.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
@Profile("!prod")
public class TestController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin() {

        Users users = Users.builder()
                .role(UserRole.ADMIN)
                .name("admin")
                .username("admin@codiyoung.com")
                .phoneNumber("01012345678")
                .password(bCryptPasswordEncoder.encode("123456"))
                .isDeleted(false)
                .build();

        userRepository.save(users);
        return ResponseEntity.ok("""
                임시 관리자계정 생성
                id : admin@codiyoung.com
                password : 123456
                """);
    }

}
