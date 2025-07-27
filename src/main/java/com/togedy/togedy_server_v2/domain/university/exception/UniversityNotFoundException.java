package com.togedy.togedy_server_v2.domain.university.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class UniversityNotFoundException extends CustomException {

    public UniversityNotFoundException() {
        super(ErrorCode.UNIVERSITY_NOT_FOUND);
    }
}
