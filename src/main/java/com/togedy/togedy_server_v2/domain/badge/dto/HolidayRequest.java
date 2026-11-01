package com.togedy.togedy_server_v2.domain.badge.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Getter
@Setter
@NoArgsConstructor
public class HolidayRequest {

    @NotNull(message = "연휴 종류는 필수 항목입니다.")
    private Long holidayKindId;

    @NotNull(message = "시작일은 필수 항목입니다.")
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수 항목입니다.")
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate endDate;
}
