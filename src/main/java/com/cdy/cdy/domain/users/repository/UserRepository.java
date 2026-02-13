package com.cdy.cdy.domain.users.repository;

import com.cdy.cdy.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {

    @Query("""

    SELECT u 
    FROM Users u
    WHERE u.username = :username

""")
    Optional<Users> findByUsername(@Param("username") String username);

}
