package com.togedy.togedy_server_v2.domain.university.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class UniversityScheduleNotFoundException extends CustomException {

    public UniversityScheduleNotFoundException() {
        super(ErrorCode.UNIVERSITY_SCHEDULE_NOT_FOUND);
    }
}
