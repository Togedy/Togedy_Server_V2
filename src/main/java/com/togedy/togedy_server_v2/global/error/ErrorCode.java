package com.togedy.togedy_server_v2.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // GLOBAL(0000)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G0000", "서버 내부에 문제가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G0001", "올바르지 않은 값 또는 형식입니다."),
    APP_CONFIG_NOT_FOUND(HttpStatus.NOT_FOUND, "G0002", "앱 설정이 존재하지 않습니다."),
    STORAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "G0003", "파일 업로드가 실패하였습니다."),

    // JWT (1000)
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "J1000", "만료된 토큰입니다."),
    JWT_UNSUPPORTED(HttpStatus.BAD_REQUEST, "J1001", "지원하지 않는 토큰입니다."),
    JWT_MALFORMED(HttpStatus.BAD_REQUEST, "J1002", "손상된 토큰입니다."),
    JWT_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "J1003", "토큰 서명이 유효하지 않습니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "J1004", "유효하지 않은 토큰입니다."),
    JWT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "J1005", "토큰을 찾을 수 없습니다."),
    JWT_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "J1006", "잘못된 토큰 형식입니다."),
    JWT_REFRESH_MISMATCH(HttpStatus.UNAUTHORIZED, "J1007", "리프레시 토큰이 서버와 일치하지 않습니다."),

    // USER (2000)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U2000", "사용자를 찾을 수 없습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "U2001", "동일한 닉네임이 존재합니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U2002", "동일한 이메일이 존재합니다."),
    USER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "U2003", "유저 접근 권한이 존재하지 않습니다."),

    // CATEGORY (3000)
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "C3000", "해당 카테고리를 찾을 수 없습니다."),
    CATEGORY_NOT_OWNED(HttpStatus.UNAUTHORIZED, "C3001", "해당 유저의 카테고리가 아닙니다."),
    DUPLICATE_CATEGORY(HttpStatus.BAD_REQUEST, "C3002", "이름과 색상이 동일한 카테고리가 존재합니다."),

    // USER_SCHEDULE (4000)
    USER_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "US4000", "해당 개인 일정을 찾을 수 없습니다."),
    USER_SCHEDULE_NOT_OWNED(HttpStatus.UNAUTHORIZED, "US4001", "해당 유저의 개인 일정이 아닙니다."),

    // UNIVERSITY (5000)
    UNIVERSITY_NOT_FOUND(HttpStatus.NOT_FOUND, "UN5000", "해당 대학을 찾을 수 없습니다."),
    INVALID_ADMISSION_TYPE(HttpStatus.BAD_REQUEST, "UN5001", "유효하지 않은 입시 유형입니다."),

    // UNIVERSITY_ADMISSION_METHOD (6000)
    UNIVERSITY_ADMISSION_METHOD_NOT_FOUND(HttpStatus.NOT_FOUND, "UAM6000", "해당 대학 입시 전형을 찾을 수 없습니다."),
    DUPLICATE_UNIVERSITY_ADMISSION_METHOD(HttpStatus.BAD_REQUEST, "UAM6001", "이미 추가한 대학 입시 전형입니다."),

    // USER_UNIVERSITY_METHOD (7000)
    USER_UNIVERSITY_METHOD_NOT_OWNED(HttpStatus.FORBIDDEN, "UUM7000", "해당 유저가 보유하지 않은 대학 입시 전형입니다."),

    // STUDY (8000)
    DUPLICATE_STUDY_NAME(HttpStatus.BAD_REQUEST, "S8000", "이름이 동일한 스터디가 존재합니다."),
    STUDY_NOT_FOUND(HttpStatus.NOT_FOUND, "S8001", "해당 스터디를 찾을 수 없습니다."),
    STUDY_LEADER_REQUIRED(HttpStatus.FORBIDDEN, "S8002", "스터디 리더만 수행할 수 있습니다."),
    STUDY_PASSWORD_REQUIRED(HttpStatus.UNPROCESSABLE_ENTITY, "S8003", "스터디 비밀번호를 입력해야 합니다."),
    STUDY_PASSWORD_MISMATCH(HttpStatus.FORBIDDEN, "S8004", "스터디 비밀번호가 일치하지 않습니다."),
    STUDY_MEMBER_REQUIRED(HttpStatus.FORBIDDEN, "S8005", "스터디 멤버만 수행할 수 있습니다."),
    STUDY_MEMBER_COUNT_EXCEEDED(HttpStatus.CONFLICT, "S8006", "스터디 멤버 수가 최대에 도달했습니다."),
    INVALID_STUDY_MEMBER_LIMIT(HttpStatus.BAD_REQUEST, "S8007", "현재 가입 인원보다 크거나 같은 최대 인원을 입력해야 합니다."),
    STUDY_LEADER_NOT_FOUND(HttpStatus.NOT_FOUND, "S8008", "해당 스터디의 리더를 찾을 수 없습니다."),
    STUDY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "S8009", "해당 스터디에 참여한 유저가 아닙니다."),
    INVALID_STUDY_TAG(HttpStatus.BAD_REQUEST, "S8010", "유효하지 않은 스터디 태그입니다."),
    STUDY_MEMBER_LIMIT_OUT_OF_RANGE(HttpStatus.CONFLICT, "S8011", "스터디 최대 인원은 1명 이상 30명 이하로 설정해야 합니다."),
    STUDY_MINIMUM_MEMBER_REQUIRED(HttpStatus.CONFLICT, "S9012", "스터디는 최소 한 명의 멤버가 존재해야 합니다."),
    STUDY_LEADER_CANNOT_REMOVE_SELF(HttpStatus.CONFLICT, "S9013", "스터디 리더는 방출할 수 없습니다."),
    STUDY_ALREADY_JOINED(HttpStatus.CONFLICT, "S9014", "이미 가입한 스터디입니다."),
    STUDY_NAME_CONTAINS_BAD_WORD(HttpStatus.BAD_REQUEST, "S9015", "스터디 이름에 비속어가 포함되어 있습니다."),
    STUDY_DESCRIPTION_CONTAINS_BAD_WORD(HttpStatus.BAD_REQUEST, "S9016", "스터디 소개에 비속어가 포함되어 있습니다."),

    // USER_STUDY(9000)
    USER_STUDY_NOT_FOUND(HttpStatus.NOT_FOUND, "US9000", "해당 유저가 가입한 스터디를 찾을 수 없습니다."),

    // KAKAO(10000)
    KAKAO_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "K10000", "카카오 토큰이 만료되었습니다."),
    INVALID_KAKAO_TOKEN(HttpStatus.UNAUTHORIZED, "K10001", "유효하지 않은 카카오 토큰입니다."),
    KAKAO_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "K10002", "카카오 계정 정보를 조회할 수 없습니다."),
    KAKAO_API_ERROR(HttpStatus.BAD_GATEWAY, "K10003", "카카오 인증 서버 통신 오류입니다."),

    // STUDY_SUBJECT(11000)
    STUDY_SUBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "SC11000", "해당 과목을 찾을 수 없습니다."),
    STUDY_SUBJECT_NOT_OWNED(HttpStatus.UNAUTHORIZED, "SC11001", "해당 유저의 과목이 아닙니다."),
    DUPLICATE_STUDY_SUBJECT(HttpStatus.BAD_REQUEST, "SC11002", "이름과 색상이 동일한 과목이 존재합니다."),
    INVALID_STUDY_SUBJECT_REORDER(HttpStatus.BAD_REQUEST, "SC11003", "과목 순서 변경 요청이 올바르지 않습니다."),
    INVALID_STUDY_SUBJECT(HttpStatus.BAD_REQUEST, "SC11004", "유효하지 않은 과목 요청입니다."),

    // STUDY_TASK(12000)
    STUDY_TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "ST12000", "해당 테스크를 찾을 수 없습니다."),
    STUDY_TASK_NOT_OWNED(HttpStatus.UNAUTHORIZED, "ST12001", "해당 유저의 테스크가 아닙니다."),
    INVALID_STUDY_TASK_NAME(HttpStatus.BAD_REQUEST, "ST12002", "테스크 이름은 비어 있을 수 없습니다."),

    // NOTICE(13000)
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "N13000", "해당 공지사항을 찾을 수 없습니다."),

    // TIMER(14000)
    TIMER_ALREADY_RUNNING(HttpStatus.CONFLICT, "T14000", "이미 진행 중인 타이머가 존재합니다."),
    TIMER_NOT_FOUND(HttpStatus.NOT_FOUND, "T14001", "해당 타이머를 찾을 수 없습니다."),
    TIMER_NOT_OWNED(HttpStatus.FORBIDDEN, "T14002", "해당 유저의 타이머가 아닙니다."),
    TIMER_ALREADY_STOPPED(HttpStatus.CONFLICT, "T14003", "이미 종료된 타이머입니다."),

    // PLANNER(15000)
    INVALID_PLANNER_IMAGE(HttpStatus.BAD_REQUEST, "PL15000", "플래너 이미지 요청이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
