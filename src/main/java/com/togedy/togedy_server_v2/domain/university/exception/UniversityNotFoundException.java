package com.togedy.togedy_server_v2.domain.university.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class UniversityNotFoundException extends CustomException {

    public UniversityNotFoundException() {
        super(ErrorCode.UNIVERSITY_NOT_FOUND);
    }
}
