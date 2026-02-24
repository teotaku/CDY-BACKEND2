package com.cdy.cdy.domain.attendance.repository;

import com.cdy.cdy.domain.attendance.entity.DailyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<DailyAttendance ,Long> {


    @Query("""
                select case when count(a) > 0 then true else false end
                from DailyAttendance a
                where a.user.id = :userId
                  and a.checkDate = :today
            """)
    boolean existsByUserAndDate(@Param("userId") Long userId,
                                @Param("today") LocalDate today);

    boolean existsByUser_IdAndCheckDate(Long userId, LocalDate checkDate);
    // (UserId 라고 붙여도 동작: existsByUserIdAndCheckDate)

    // 한 달 범위 조회 (양끝 포함)
    @Query("""
    SELECT d
    FROM DailyAttendance d
    WHERE d.user.id = :userId
      AND d.checkDate BETWEEN :start AND :end
""")
    List<DailyAttendance> findAllByUser_IdAndCheckDateBetween(Long userId, LocalDate start, LocalDate end);


}