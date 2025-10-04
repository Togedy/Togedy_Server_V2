package com.togedy.togedy_server_v2.global.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StorageUploadFailedException extends CustomException {

    public StorageUploadFailedException() {
        super(ErrorCode.STORAGE_UPLOAD_FAILED);
    }
}
