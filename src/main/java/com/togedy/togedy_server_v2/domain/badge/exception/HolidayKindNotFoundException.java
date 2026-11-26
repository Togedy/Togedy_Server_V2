package com.togedy.togedy_server_v2.domain.badge.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class HolidayKindNotFoundException extends CustomException {

    public HolidayKindNotFoundException() {
        super(ErrorCode.HOLIDAY_KIND_NOT_FOUND);
    }
}
