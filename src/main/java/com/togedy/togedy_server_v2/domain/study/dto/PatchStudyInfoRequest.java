package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchStudyInfoRequest {

    private LocalTime goalTime;
    private String studyName;
    private String studyDescription;
    private Integer memberLimit;
    private String studyTag;
    private MultipartFile studyImage;
    private String password;
    private Boolean isDuplicate;

}
