package com.togedy.togedy_server_v2.domain.university.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class DuplicateUniversityAdmissionMethodException extends CustomException {

    public DuplicateUniversityAdmissionMethodException() {
        super(ErrorCode.DUPLICATE_UNIVERSITY_ADMISSION_METHOD);
    }
}
