package com.cdy.cdy.domain.auth;


import com.cdy.cdy.domain.users.dto.UserRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;


    @Operation(summary = "로그인(개발용)")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated(UserRequestDto.loginGroup.class)
                                   @RequestBody UserRequestDto userRequestDto) {

        String jwt = authService.login(userRequestDto);
        return ResponseEntity.ok(jwt);

    }

    @Operation(summary = "회원가입(개발용)")
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Validated(UserRequestDto.addGroup.class)
                                    @RequestBody UserRequestDto dto) {

        authService.signUp(dto);
        return ResponseEntity.ok("회원가입 완료");
    }
}
