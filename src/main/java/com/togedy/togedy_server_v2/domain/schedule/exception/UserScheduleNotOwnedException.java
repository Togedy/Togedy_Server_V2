package com.togedy.togedy_server_v2.domain.schedule.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class UserScheduleNotOwnedException extends CustomException {

    public UserScheduleNotOwnedException() {
        super(ErrorCode.USER_SCHEDULE_NOT_OWNED);
    }
}
