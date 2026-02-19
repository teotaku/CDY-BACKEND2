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


    /**
     *  불린값 반환 3가지 케이스
     *  case when + exists
     *  case when + count
     *  exists + native query
     */
    @Query("""
SELECT CASE WHEN COUNT(u) > 0 
       THEN true ELSE false END
FROM Users u
WHERE u.username = :username
""")
    boolean existsByUsername(String username);

//    @Query("""
//
//                SELECT
//                CASE WHEN
//                EXISTS(
//                SELECT 1
//                FROM Users u
//                WHERE u.username = :username
//               )
//                THEN true ELSE false
//                END
//                FROM Users x
//            """)
//    boolean existsByUsername(@Param("username") String username);

//    @Query(value = """
//SELECT EXISTS(
//    SELECT 1
//    FROM users u
//    WHERE u.username = :username
//)
//""", nativeQuery = true)
//    boolean existsByUsername(@Param("username") String username);
}
