package com.togedy.togedy_server_v2.domain.study.api;

import com.togedy.togedy_server_v2.domain.study.application.StudyService;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyResponse;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping(value = "/studies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> createStudy(@ModelAttribute PostStudyRequest request,
                                         @AuthenticationPrincipal AuthUser user) {
        studyService.generateStudy(request, user.getId());
        return ApiUtil.successOnly();
    }

    @GetMapping("/studies/{studyId}")
    public ApiResponse<GetStudyResponse> readStudy(@PathVariable Long studyId) {
        GetStudyResponse response = studyService.findStudy(studyId);
        return ApiUtil.success(response);
    }

    @DeleteMapping("studies/{studyId}")
    public ApiResponse<Void> deleteStudy(@PathVariable Long studyId,
                                         @AuthenticationPrincipal AuthUser user) {
        studyService.removeStudy(studyId, user.getId());
        return ApiUtil.successOnly();
    }
}
