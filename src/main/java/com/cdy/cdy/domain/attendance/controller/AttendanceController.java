package com.cdy.cdy.domain.attendance.controller;


import com.cdy.cdy.domain.attendance.dto.MonthCalendarResponse;
import com.cdy.cdy.domain.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;


@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {


    private final AttendanceService attendanceService;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");


    @Operation(summary = "출석체크 로직", description = """
            로그인된 유저의 정보로
            오늘 출석체크 완료 로직
            """)
    @PostMapping("/check-today")
    public ResponseEntity<?> checkToday(Authentication authentication) {

        log.info("[AttendanceController] 출석 요청 , username : {}",authentication.getName());

        attendanceService.checkToday(authentication.getName());
        return ResponseEntity.ok("출석체크 완료");

    }

    @Operation(summary = "출석부 조회 로직", description = """
            로그인 된 유저의 출석부 로직 조회.
            파라미터 month는 [YYYY-MM] 형식이어야합니다. 
            출석된 날짜는 checked -> true 출석되지 않은 날짜는 false 반환
            """)
    @GetMapping("/calendar")
    public ResponseEntity<?> calendar(Authentication authentication,
                                      @RequestParam(name = "month", required = false) String month) {

        YearMonth ym;

        try {
            ym = (month == null || month.isBlank())
                    ? YearMonth.now(KST)
                    : YearMonth.parse(month);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("month는 YYYY-MM 형식이어야 합니다.");

        }

        MonthCalendarResponse result = attendanceService.getMonth(authentication.getName(), ym);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "출석체크 중복 확인", description = """
            로그인 한 유저가 오늘 출석을 했는지 확인하는 로직
            출석했으면 true, 출석을 안 했으면 false 반환
            """)
    @GetMapping("/isCheckedToday")
    public ResponseEntity<?> isCheckedToday(Authentication authentication) {

        Boolean result = attendanceService.isCheckedToday(authentication.getName());
        return ResponseEntity.ok(result);

    }
}
