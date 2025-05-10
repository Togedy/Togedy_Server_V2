package com.togedy.togedy_server_v2.domain.university.api;

import com.togedy.togedy_server_v2.domain.university.application.UniversityService;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityScheduleResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/calendars/universities")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    @GetMapping("")
    public ApiResponse<List<GetUniversityScheduleResponse>> readUniversityScheduleList(@RequestParam(name = "name") String name){
        List<GetUniversityScheduleResponse> response = universityService.findUniversityScheduleList(name);
        return ApiUtil.success(response);
    }

}
