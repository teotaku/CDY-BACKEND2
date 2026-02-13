package com.cdy.cdy.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshRepositoryJDBC {

    private final NamedParameterJdbcTemplate template;


    public int save(RefreshEntity entity) {

        String sql = """
                
                INSERT INTO jwt_refresh_entity
                (username,refresh,created_at)
                VALUES
                (
                :username,
                :refresh,
                NOW()
                )
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", entity.getUsername());
        params.addValue("refresh", entity.getRefresh());

        int result = template.update(sql, params);
        return result;
    }

    public int existsByRefresh(String refreshToken) {
        String sql = """
                SELECT CASE WHEN
                COUNT(*) > 0 THEN 1
                ELSE FALSE 
                END
                FROM jwt_refresh_entity jre
                WHERE jre.refresh = :refreshToken
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("refreshToken", refreshToken);
        Integer result = template.queryForObject(sql, params, Integer.class);
        return result;
    }

    public int delete(String refreshToken) {

        String sql = """
                DELETE 
                FROM jwt_refresh_entity jre
                WHERE jre.refresh = :refreshToken
                """;
        Map<String, String> param = Map.of("refreshToken", refreshToken);
        int result = template.update(sql, param);
        return result;

    }

    public int deleteByUsername(String username) {

        String sql = """
                
                DELETE 
                FROM jwt_refresh_entity jre
                WHERE jre.username = :username
                """;

        Map<String, String> param = Map.of("username", username);
        int result = template.update(sql, param);
        return result;

    }

    public Optional<RefreshEntity> findByUsername(String username) {

        String sql = """
                SELECT *
                FROM jwt_refresh_entity jre
                WHERE jre.username = :username
                """;

        Map<String, String> param = Map.of("username", username);
        Optional<RefreshEntity> result = template.query(sql, param, (rs, roNum) ->
                RefreshEntity.builder()
                        .username(rs.getNString("username"))
                        .refresh(rs.getNString("refresh"))
                        .build()
        ).stream().findFirst();

        return result;
    }

    public void deleteByCreatedDateBefore(LocalDateTime cutoff) {

        String sql = """
                DELETE 
                FROM jwt_refresh_entity jre
                WHERE jre.created_at < :cutoff
                """;
        Map<String, LocalDateTime> param = Map.of("cutoff", cutoff);
        int result = template.update(sql, param);
    }
}
