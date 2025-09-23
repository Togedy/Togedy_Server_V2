package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostStudyRequest {

    private Integer goalTime;

    private String studyName;

    private String studyDescription;

    private Integer studyMemberLimit;

    private String studyTag;

    private MultipartFile studyImage;

    private String studyPassword;

}
