package com.cdy.cdy.domain.attendance;

import com.cdy.cdy.domain.attendance.dto.CalendarDay;
import com.cdy.cdy.domain.attendance.dto.MonthCalendarResponse;
import com.cdy.cdy.domain.attendance.entity.DailyAttendance;
import com.cdy.cdy.domain.attendance.repository.AttendanceRepository;
import com.cdy.cdy.domain.attendance.service.AttendanceService;
import com.cdy.cdy.domain.users.entity.Users;
import com.cdy.cdy.domain.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AttenDanceServiceTest {


    @Mock
    AttendanceRepository attendanceRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    AttendanceService attendanceService;


    @Test
    void 출석체크시_db저장_완료() {

        //given

        ArgumentCaptor<DailyAttendance> captor = ArgumentCaptor.forClass(DailyAttendance.class);
        given(attendanceRepository.existsByUser_IdAndCheckDate(anyLong(), any()))
                .willReturn(false);

        Users users = Users.builder()
                .username("username")
                .id(1L)
                .build();


        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));

        //when

        attendanceService.checkToday(users.getUsername());


        //then
        verify(attendanceRepository).saveAndFlush(captor.capture());
        DailyAttendance value = captor.getValue();
        assertThat(value.getUser()).isEqualTo(users);
        assertThat(value.getCheckDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void 출석체크시_이미_완료된_출석체크_존재하면_에러발생() {

        //given
        Users users = Users.builder()
                .id(1L)
                .username("username")
                .build();

        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));
        given(attendanceRepository.existsByUser_IdAndCheckDate(anyLong(), any()))
                .willReturn(true);

        //when & then
        assertThatThrownBy(() ->
                attendanceService.checkToday("username")
        ).isInstanceOf(IllegalArgumentException.class);


    }

    /**'
     * 출석부 조회 로직
     */

    @Test
    void 출석여부_boolean으로_달력_정상조회() {

        //given
        Users users = Users.builder()
                .username("username")
                .id(1L)
                .build();
        YearMonth yearMonth = YearMonth.of(2026, 02);

        LocalDate d1 = LocalDate.of(2026, 2, 3);
        LocalDate d2 = LocalDate.of(2026, 2, 10);

        DailyAttendance dailyAttendance1 = DailyAttendance.builder()
                .checkDate(d1)
                .user(users)
                .build();

        DailyAttendance dailyAttendance2= DailyAttendance.builder()
                .checkDate(d2)
                .user(users)
                .build();

        given(attendanceRepository.findAllByUser_IdAndCheckDateBetween
                (users.getId(), yearMonth.atDay(1), yearMonth.atEndOfMonth()))
                .willReturn(List.of(dailyAttendance1, dailyAttendance2));
        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));
        //when

        MonthCalendarResponse result = attendanceService.getMonth(users.getUsername(), yearMonth);


        //then
        assertThat(result.getDays())
                .anyMatch(d -> d.getDate().equals(d1) && d.isChecked());

        assertThat(result.getDays())
                .anyMatch(d -> d.getDate().equals(d2) && d.isChecked());

    }

    @Test
    void 출석하지않은날_boolean_확인() {

        //given
        Users users = Users.builder()
                .username("username")
                .id(1L)
                .build();
        YearMonth yearMonth = YearMonth.of(2026, 02);

        LocalDate d1 = LocalDate.of(2026, 2, 3);
        LocalDate d2 = LocalDate.of(2026, 2, 10);



        given(attendanceRepository.findAllByUser_IdAndCheckDateBetween
                (users.getId(), yearMonth.atDay(1), yearMonth.atEndOfMonth()))
                .willReturn(List.of());
        given(userRepository.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));
        //when

        MonthCalendarResponse result = attendanceService.getMonth(users.getUsername(), yearMonth);


        //then
        CalendarDay calendarDay = result.getDays().get(0);
        //달력의 년도와 월 정상 반환 확인
        assertThat(result.getMonth()).isEqualTo(yearMonth.toString());
        //출석하지않은날 checked 값 false 확인
        assertThat(calendarDay.isChecked()).isEqualTo(false);


    }

}
