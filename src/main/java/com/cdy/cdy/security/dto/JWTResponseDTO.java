package com.cdy.cdy.security.dto;

public record JWTResponseDTO(
        String accessToken,
        String refreshToken
) {
}
