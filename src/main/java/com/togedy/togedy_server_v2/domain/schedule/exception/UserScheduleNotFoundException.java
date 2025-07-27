package com.togedy.togedy_server_v2.domain.schedule.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class UserScheduleNotFoundException extends CustomException {

    public UserScheduleNotFoundException() {
        super(ErrorCode.USER_SCHEDULE_NOT_FOUND);
    }
}
