package com.togedy.togedy_server_v2.domain.badge.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class BadgeNotFoundException extends CustomException {

    public BadgeNotFoundException() {
        super(ErrorCode.BADGE_NOT_FOUND);
    }
}
