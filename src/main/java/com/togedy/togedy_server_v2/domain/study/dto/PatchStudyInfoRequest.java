package com.togedy.togedy_server_v2.domain.study.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchStudyInfoRequest {

    private String studyName;

    private String studyDescription;

    private String studyTag;

    private MultipartFile studyImage;

    private String studyPassword;

    @JsonAlias({"duplicate", "isDuplicate"})
    private Boolean duplicate;

}
