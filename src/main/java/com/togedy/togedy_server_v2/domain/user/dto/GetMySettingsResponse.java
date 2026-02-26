package com.togedy.togedy_server_v2.domain.user.dto;

import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMySettingsResponse {

    private Boolean pushNotificationEnabled;

    private Boolean marketingConsented;

    private String userEmail;

    public static GetMySettingsResponse from(User user) {
        return GetMySettingsResponse.builder()
                .pushNotificationEnabled(user.isPushNotificationEnabled())
                .marketingConsented(user.isMarketingConsented())
                .userEmail(user.getEmail())
                .build();
    }
}
