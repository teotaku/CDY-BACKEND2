package com.cdy.cdy.security.jwt;

import com.cdy.cdy.security.dto.JWTResponseDTO;
import com.cdy.cdy.security.dto.RefreshRequestDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RefreshRepositoryJDBC refreshRepositoryJDBC;
    private final RefreshRepositoryJPA refreshRepositoryJPA;
    private final JwtUtil jwtUtil;

    @Transactional
    public void addRefresh(String username, String refreshToken) {
        RefreshEntity entity =
                RefreshEntity.builder()
                        .username(username)
                        .refresh(refreshToken)
                        .build();
        int result = refreshRepositoryJDBC.save(entity);
        if (result == 0) {
            throw new RuntimeException("サーバーエラーが発生しました。");
        }
    }
    @Transactional(readOnly = true)
    public Boolean existsByRefresh(String refreshToken) {
        int result = refreshRepositoryJDBC.existsByRefresh(refreshToken);
        if (result == 1) {
            return true;
        } else return false;
    }

    @Transactional
    public void deleteRefresh(String refreshToken) {
        int result = refreshRepositoryJDBC.delete(refreshToken);
        if (result == 0) {
            throw new RuntimeException("サーバーエラーが発生しました。");
        }
    }

    @Transactional
    public void deleteRefreshByUsername(String username) {

        int result = refreshRepositoryJDBC.deleteByUsername(username);
        if (result == 0) {
            throw new RuntimeException("サーバーエラーが発生しました。");
        }

    }

    @Transactional
    public JWTResponseDTO refreshRotate(RefreshRequestDto dto) {

        String refreshToken = dto.getRefreshToken();

        // Refresh 토큰 검증
        Boolean isValid = jwtUtil.isValid(refreshToken, false);
        if (!isValid) {
            throw new RuntimeException("유효하지 않은 refreshToken입니다.");
        }

        // RefreshEntity 존재 확인 (화이트리스트)
        if (!existsByRefresh(refreshToken)) {
            throw new RuntimeException("유효하지 않은 refreshToken입니다.");
        }

        // 정보 추출
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 토큰 생성
        String newAccessToken = jwtUtil.createJWT(username, role, true);
        String newRefreshToken = jwtUtil.createJWT(username, role, false);

        // 기존 Refresh 토큰 DB 삭제 후 신규 추가
        RefreshEntity newRefreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(newRefreshToken)
                .build();

        deleteRefresh(refreshToken);
        refreshRepositoryJDBC.save(newRefreshEntity);

        return new JWTResponseDTO(newAccessToken, newRefreshToken);
    }

    @Transactional
    public JWTResponseDTO cookie2Header(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        // 쿠키 리스트
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("쿠키가 존재하지 않습니다.");
        }

        // Refresh 토큰 획득
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        if (refreshToken == null) {
            throw new RuntimeException("refreshToken 쿠키가 없습니다.");
        }

        // Refresh 토큰 검증
        Boolean isValid = jwtUtil.isValid(refreshToken, false);
        if (!isValid) {
            throw new RuntimeException("유효하지 않은 refreshToken입니다.");
        }

        // 정보 추출
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 토큰 생성
        String newAccessToken = jwtUtil.createJWT(username, role, true);
        String newRefreshToken = jwtUtil.createJWT(username, role, false);

        // 기존 Refresh 토큰 DB 삭제 후 신규 추가
        RefreshEntity newRefreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(newRefreshToken)
                .build();

        deleteRefresh(refreshToken);
        refreshRepositoryJPA.flush(); // 같은 트랜잭션 내부라 : 삭제 -> 생성 문제 해결
        refreshRepositoryJDBC.save(newRefreshEntity);

        // 기존 쿠키 제거
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(10);
        response.addCookie(refreshCookie);

        return new JWTResponseDTO(newAccessToken, newRefreshToken);
    }



}
