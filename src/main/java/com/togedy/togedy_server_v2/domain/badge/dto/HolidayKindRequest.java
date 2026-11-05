package com.togedy.togedy_server_v2.domain.badge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class HolidayKindRequest {

    @NotBlank(message = "연휴 종류 이름은 필수 항목입니다.")
    @Size(max = 50, message = "연휴 종류 이름은 50자 이하여야 합니다.")
    private String name;

    private boolean badgeEnabled;

    private MultipartFile badgeImage;

    public boolean hasBadgeImage() {
        return badgeImage != null && !badgeImage.isEmpty();
    }
}
