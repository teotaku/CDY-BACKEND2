package com.cdy.cdy.common.test;

import com.cdy.cdy.domain.users.repository.UserRepository;
import com.cdy.cdy.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.expiration}")
    private long accessExpireMs;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MailService mailService;
    private final EmailVerificationRepository emailVerificationRepository;
    //회원가입
    @Transactional
    public void Join(JoinDto joinDto) {




        String username = joinDto.getUsername();
        String password = joinDto.getPassword();
        UserType userType = joinDto.getUserType();
        String phone = joinDto.getPhone();

        if (userType == null) {
            throw new IllegalArgumentException("userType은 필수값입니다.");
        }


       Long isExist = userRepository.existByUsername(username);
        //이미 존재하는지 확인
        if (isExist == 1L) {
            throw new IllegalStateException("이미 존재하는 유저입니다.");
        }
        //같은 이메일 쓰는 유저있는지 확인
        if (userRepository.isExistEmail(joinDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }



        userRepository.isExistEmail(joinDto.getEmail());

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .role(UserRole.USER)
                .email(joinDto.getEmail())
                .name(joinDto.getName())
                .userType(userType)
                .phone(phone)
                .build();

        userRepository.save(user);

    }
    //refresh 토큰 재발급
    @Transactional
    public ReissueTokens reissue(String refresh) {

        //  만료 검사
        jwtUtil.isExpired(refresh);

        //  refresh 토큰인지 category 확인
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            throw new IllegalArgumentException("invalid refresh token");
        }

        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            throw new IllegalArgumentException("invalid refresh token");
        }


        //  사용자 정보 추출
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);




        //  새로운 access token 생성 후 반환
        String accessToken =
                jwtUtil.createJwt("access", username, role, accessExpireMs);
        String newRefresh =
                jwtUtil.createJwt("refresh", username, role, 86400000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, 86400000L);


        return new ReissueTokens(accessToken, newRefresh);


    }
    //refresh토큰 저장
    @Transactional
    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);


        RefreshEntity refreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshRepository.save(refreshEntity);
    }
    //로그인
    @Transactional
    public ResponseLoginWithToken login(LoginRequest loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();


        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new EntityNotFoundException("존재하지 않는 아이디입니다 "));

        if (!bCryptPasswordEncoder.matches(password, userEntity.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 다릅니다. " );
        }

        UserRole role = userEntity.getRole();
        UserType userType = userEntity.getUserType();

        String access = jwtUtil.createJwt("access", username, role.name(), accessExpireMs);
        String refresh = jwtUtil.createJwt("refresh", username, role.name(), 86400000L);

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(refresh)
                .expiration("86400000")
                .build();

        refreshRepository.save(refreshEntity);

        TokenResponse tokenResponse = new TokenResponse(access, refresh);


        return new ResponseLoginWithToken(tokenResponse, userEntity.getMustChangePassword());

    }
    //유저타입 변경
    @Transactional
    public void changeUserType(ChangeTypeDto changeTypeDto,String username) {


        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));


        if (username == userEntity.getUsername()) {
            throw new IllegalArgumentException("유저타입은 본인만 변경할 수 있습니다.");
        }


        if (changeTypeDto.getChangeUserType().name() != "CUSTOMER"
                && changeTypeDto.getChangeUserType().name() != "OWNER"
        ) {
            throw new IllegalArgumentException("UserType은 CUSTOMER이거나 OWNER여야 합니다. 잘못된 값: " +
                    changeTypeDto.getChangeUserType().name());

        }

        userRepository.updateUserType(userEntity.getId(),
                changeTypeDto.getChangeUserType().name());

    }
    //유저정보조회
    public GetUserInfo getUserInfo(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow
                (() -> new EntityNotFoundException("존재하지않는 유저입니다."));

        GetUserInfo userInfo = userRepository.getUserInfo(userEntity.getId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지않는유저"));

        return userInfo;

    }
    //이메일 전송코드 확인 verify
    @Transactional
    public void
    verifyEmail(String email, String code) {

        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("인증 요청이 없습니다."));


        if (emailVerification.isExpired()) {
            throw new IllegalStateException("인증 시간이 만료되었습니다");
        }
        if (!emailVerification.getCode().equals(code)) {
            throw new IllegalStateException("인증 코드가 일치하지 않습니다.");
        }

        emailVerification.verify();

    }


    //유저 이름 찾기
    @Transactional
    public void findUsername(String email) {

        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("이메일 인증 필요"));

        if (!emailVerification.isVerified()) {
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");
        }

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("가입된 계정이 없습니다."));

        mailService.sendUsername(email, userEntity.getUsername());
        emailVerificationRepository.delete(emailVerification);

    }

    //유저 비밀번호찾기
    @Transactional
    public void findPassword(String email) {

        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("이메일 인증 필요"));

        if (!emailVerification.isVerified()) {
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");
        }

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("가입된 계정이 없습니다."));
        String tempPassword = createTempPassword();
        String encodePassword = bCryptPasswordEncoder.encode(tempPassword);
        userEntity.changePassword(encodePassword);

        mailService.sendPassword(email, tempPassword);
        emailVerificationRepository.delete(emailVerification);
        userEntity.changeMustChangePassword(true);
        userRepository.save(userEntity);

    }

    //임시 비밀번호 생성
    private String createTempPassword() {
        // 사용할 문자 조합
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // 암호학적으로 안전한 난수 생성기
        SecureRandom random = new SecureRandom();

        // 결과 문자열 생성
        StringBuilder sb = new StringBuilder();

        // 10자리 비밀번호 생성
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(chars.length()); // 0~chars.length()-1 범위 랜덤
            sb.append(chars.charAt(index)); // 해당 인덱스의 문자 추가
        }

        return sb.toString();

    }
        //비밀 번호 변경
    public void changePassword(String username ,ChangePassword changePassword) {


        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저"));

        if (userEntity.getPassword().equals(bCryptPasswordEncoder.encode(changePassword.getCurrentPassword()))) {
            throw new IllegalArgumentException("사용하고 있는 비밀번호가 치하지않습니다.");
        }

        if (!bCryptPasswordEncoder.matches(
                changePassword.getCurrentPassword(),
                userEntity.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (changePassword == null ||
                changePassword.getNewPassword() == null ||
                changePassword.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("변경할 비밀번호를 입력해주세요.");
        }

        String encodedPassword
                = bCryptPasswordEncoder.encode(changePassword.getNewPassword());



        userEntity.changePassword(encodedPassword);
        userEntity.changeMustChangePassword(false);
        userRepository.save(userEntity);

    }
    //소셜로그인 유저정보 반환 username,id email
    public ResponseOauth2Info getOauth2UserInfo(String username) {


        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저"));


        return new ResponseOauth2Info(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail());

    }
}
