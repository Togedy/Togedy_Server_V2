package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Getter
@Builder
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
