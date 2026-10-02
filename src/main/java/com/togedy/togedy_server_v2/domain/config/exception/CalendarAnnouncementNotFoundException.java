package com.togedy.togedy_server_v2.domain.config.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class CalendarAnnouncementNotFoundException extends CustomException {

    public CalendarAnnouncementNotFoundException() {
        super(ErrorCode.CALENDAR_ANNOUNCEMENT_NOT_FOUND);
    }
}
