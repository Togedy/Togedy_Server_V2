package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class DuplicateStudyCategoryException extends CustomException {

    public DuplicateStudyCategoryException() { super(ErrorCode.DUPLICATE_STUDY_CATEGORY); }
}
