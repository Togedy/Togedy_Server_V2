package com.togedy.togedy_server_v2.domain.schedule.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class UserScheduleNotFoundException extends CustomException {

    public UserScheduleNotFoundException() {
        super(ErrorCode.USER_SCHEDULE_NOT_FOUND);
    }
}
