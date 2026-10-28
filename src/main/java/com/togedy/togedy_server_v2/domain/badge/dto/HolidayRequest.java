package com.togedy.togedy_server_v2.domain.badge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "연휴명은 필수 항목입니다.")
    @Size(max = 50, message = "연휴명은 50자 이하여야 합니다.")
    private String name;

    @NotNull(message = "시작일은 필수 항목입니다.")
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수 항목입니다.")
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate endDate;
}
