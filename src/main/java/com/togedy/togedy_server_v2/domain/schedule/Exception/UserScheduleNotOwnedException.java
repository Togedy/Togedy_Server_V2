package com.togedy.togedy_server_v2.domain.schedule.Exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class UserScheduleNotOwnedException extends CustomException {

    public UserScheduleNotOwnedException() {
        super(ErrorCode.USER_SCHEDULE_NOT_OWNED);
    }
}
