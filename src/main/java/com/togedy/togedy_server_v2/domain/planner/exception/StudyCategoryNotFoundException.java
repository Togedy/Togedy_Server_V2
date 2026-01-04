package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyCategoryNotFoundException extends CustomException {

    public StudyCategoryNotFoundException() { super(ErrorCode.STUDY_CATEGORY_NOT_FOUND); }
}
