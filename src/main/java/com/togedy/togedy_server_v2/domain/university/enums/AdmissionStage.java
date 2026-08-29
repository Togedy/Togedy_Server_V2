package com.togedy.togedy_server_v2.domain.university.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdmissionStage {
    원서접수(1),
    서류제출(2),
    합격발표(3);

    private final int order;
}
