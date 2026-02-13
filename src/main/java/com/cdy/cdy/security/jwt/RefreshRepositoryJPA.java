package com.cdy.cdy.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshRepositoryJPA extends JpaRepository<RefreshEntity,Long> {
}
