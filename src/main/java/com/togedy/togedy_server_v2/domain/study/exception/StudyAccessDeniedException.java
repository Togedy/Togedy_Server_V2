package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyAccessDeniedException extends CustomException {

    public StudyAccessDeniedException() {
        super(ErrorCode.STUDY_ACCESS_DENIED);
    }
}
