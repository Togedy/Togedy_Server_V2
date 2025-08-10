package com.togedy.togedy_server_v2.domain.university.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class DuplicateUniversityAdmissionMethodException extends CustomException {

    public DuplicateUniversityAdmissionMethodException() {
        super(ErrorCode.DUPLICATE_UNIVERSITY_ADMISSION_METHOD);
    }
}
