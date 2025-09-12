package com.togedy.togedy_server_v2.domain.study.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostStudyRequest {

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime goalTime;

    private String studyName;

    private String studyDescription;

    private Integer studyMemberLimit;

    private String studyTag;

    private MultipartFile studyImage;

    private String studyPassword;

}
