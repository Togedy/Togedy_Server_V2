package com.togedy.togedy_server_v2.domain.university.enums;

import com.togedy.togedy_server_v2.domain.university.exception.InvalidAdmissionTypeException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum AdmissionType {
    EARLY_DECISION("수시"),
    REGULAR_DECISION("정시"),
    TOTAL("전체");

    private final String value;

    public static AdmissionType from(String text) {
        return Arrays.stream(values())
                .filter(t -> t.value.equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(InvalidAdmissionTypeException::new);
    }

    public static String ofValue(String text) {
        AdmissionType type = from(text);
        return type == TOTAL ? null : type.value;
    }
}
