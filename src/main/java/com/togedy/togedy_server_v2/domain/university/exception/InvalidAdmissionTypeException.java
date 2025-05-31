package com.togedy.togedy_server_v2.domain.university.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class InvalidAdmissionTypeException extends CustomException {

    public InvalidAdmissionTypeException() {
        super(ErrorCode.INVALID_ADMISSION_TYPE);
    }
}
