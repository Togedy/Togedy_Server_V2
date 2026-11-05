package com.togedy.togedy_server_v2.domain.badge.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class HolidayAlreadyEndedException extends CustomException {

    public HolidayAlreadyEndedException() {
        super(ErrorCode.HOLIDAY_ALREADY_ENDED);
    }
}
