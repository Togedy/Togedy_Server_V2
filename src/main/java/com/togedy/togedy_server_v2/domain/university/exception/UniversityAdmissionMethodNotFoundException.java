package com.togedy.togedy_server_v2.domain.university.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class UniversityAdmissionMethodNotFoundException extends CustomException {

    public UniversityAdmissionMethodNotFoundException() {
        super(ErrorCode.UNIVERSITY_ADMISSION_METHOD_NOT_FOUND);
    }
}
