package com.togedy.togedy_server_v2.domain.badge.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetBadgeResponse {

    private Long badgeId;
    private String badgeName;
    private String badgeImageUrl;
    private String badgeDescription;
    private LocalDate acquiredDate;

    public static GetBadgeResponse of(
            Long badgeId,
            String badgeName,
            String badgeImageUrl,
            String badgeDescription,
            LocalDate acquiredDate
    ) {
        return GetBadgeResponse.builder()
                .badgeId(badgeId)
                .badgeName(badgeName)
                .badgeImageUrl(badgeImageUrl)
                .badgeDescription(badgeDescription)
                .acquiredDate(acquiredDate)
                .build();
    }
}
