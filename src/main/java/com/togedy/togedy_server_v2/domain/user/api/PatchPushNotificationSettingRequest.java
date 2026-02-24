package com.togedy.togedy_server_v2.domain.user.api;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatchPushNotificationSettingRequest {

    private Boolean pushNotificationEnabled;

}
