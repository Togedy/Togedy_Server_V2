package com.togedy.togedy_server_v2.domain.badge.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class HolidayKindInUseException extends CustomException {

    public HolidayKindInUseException() {
        super(ErrorCode.HOLIDAY_KIND_IN_USE);
    }
}
