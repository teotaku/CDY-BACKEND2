package com.cdy.cdy.common.test;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "Oauth2 정보제공용", description = "id, username, email 반환")
    @GetMapping("/me")
    public ResponseEntity<ResponseOauth2Info> me(Authentication authentication) {


        ResponseOauth2Info result = authService.getOauth2UserInfo(authentication.getName());
        return ResponseEntity.ok(result);

    }

    @Operation(summary = "회원가입", description = """
            username(email),phone,UserType(CUSTOMER, OWNER고정),name(유저이름)
            값을 받고 회원가입 로직 진행
            UserType은 필수값(없으면 에러 반환)
            """)
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinDto joinDto) {

        authService.Join(joinDto);
        return ResponseEntity.ok("회원가입이 성공했습니다.");
    }

    @Operation(summary = "로그인", description = """
                username(email), password 값으로 받아서 로그인진행,
                로그인 진행후 access Token은 body , refresh Token은 쿠키로 반환
            """)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletResponse response) {
        ResponseLoginWithToken responseLoginWithToken = authService.login(loginRequest);
        Cookie refresh = createCookie
                ("refresh", responseLoginWithToken.getTokenResponse().getRefresh(), 24 * 60 * 60);
        response.addCookie(refresh);

        return ResponseEntity.ok(new LoginResponse(responseLoginWithToken.getTokenResponse().getAccess(),
                        responseLoginWithToken.getMustChangePassword()
                )
        );
    }

    @Operation(summary = "아이디 찾기로직", description = """
            이메일 코드 검증 선행후 검증이 완료됐으면 가입시 이메일로 아이디전송,
            인증코드 단계에서 검증실패면 400 에러발생 
            """)
    @PostMapping("/findUsername")
    public ResponseEntity<?> findUsername(@RequestBody EmailRequest request) {


        authService.findUsername(request.getEmail());
        return ResponseEntity.ok("아이디가 이메일로 전송되었습니다.");
    }

    @Operation(summary = "비밀번호 찾기로직", description = """
            이메일 코드 검증 선행후 검증이 완료됐으면 가입시 이메일로 임시비밀번호전송.
             인증코드 단계에서 검증실패면 400 에러발생 
            """)
    @PostMapping("/findPassword")
    public ResponseEntity<?> findPassword(@RequestBody EmailRequest request) {
        authService.findPassword(request.getEmail());
        return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
    }

    private Cookie createCookie(String key, String value, int maxAge) {


        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);   // JS 접근 X
        cookie.setSecure(true);     // HTTPS 환경이면 true
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

}
