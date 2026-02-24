package com.cdy.cdy.domain.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MonthCalendarResponse {
    @Schema(description = "조회된 달력의 월",example = "2026-02")
    private String month;
    @Schema(description = "해당 월의 일자들을 list로 반환")
    private List<CalendarDay> days;
}