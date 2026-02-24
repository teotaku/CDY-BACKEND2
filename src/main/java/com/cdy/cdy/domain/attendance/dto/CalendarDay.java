package com.cdy.cdy.domain.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
public class CalendarDay {
    @Schema(description = "날짜",example = "2026-02-16")
    private LocalDate date;
    @Schema(description = "출석이 되었는지 여부",example = "true")
    private boolean checked; // 출석 여부
}