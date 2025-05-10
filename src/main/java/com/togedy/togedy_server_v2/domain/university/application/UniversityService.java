package com.togedy.togedy_server_v2.domain.university.application;

import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityScheduleRepository;
import com.togedy.togedy_server_v2.domain.university.dto.AdmissionTypeDto;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityScheduleResponse;
import com.togedy.togedy_server_v2.domain.university.dao.UniversityScheduleRepository;
import com.togedy.togedy_server_v2.domain.university.dto.PostUniversityScheduleRequest;
import com.togedy.togedy_server_v2.domain.university.dto.UniversityScheduleDto;
import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversitySchedule;
import com.togedy.togedy_server_v2.domain.university.entity.UserUniversitySchedule;
import com.togedy.togedy_server_v2.domain.university.exception.UniversityScheduleNotFoundException;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityScheduleRepository universityScheduleRepository;
    private final UserUniversityScheduleRepository userUniversityScheduleRepository;
    private final UserRepository userRepository;

    public List<GetUniversityScheduleResponse> findUniversityScheduleList(String namePart) {
        List<UniversitySchedule> universitiScheduleList =
                universityScheduleRepository.findByUniversityNameLikeAndYear(namePart, 2025);

        Map<University, List<UniversitySchedule>> universityListMap = universitiScheduleList.stream()
                .collect(Collectors.groupingBy(
                        us -> us.getAdmissionMethod().getUniversity(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<GetUniversityScheduleResponse> response = new ArrayList<>();
        universityListMap.forEach((uni, schedules) -> {
            Map<String, List<UniversitySchedule>> admissionListMap = schedules.stream()
                    .collect(Collectors.groupingBy(
                            us -> us.getAdmissionMethod().getName(),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            List<AdmissionTypeDto> admissionList = admissionListMap.entrySet().stream()
                    .map(e -> {
                        List<UniversityScheduleDto> scheduleDtoList = e.getValue().stream()
                                .map(UniversityScheduleDto::from)
                                .collect(Collectors.toList());
                        return AdmissionTypeDto.of(e.getKey(), scheduleDtoList);
                    })
                    .collect(Collectors.toList());

            response.add(GetUniversityScheduleResponse.of(uni, admissionList));
        });

        return response;
    }

    @Transactional
    public void generateUserUniversitySchedule(PostUniversityScheduleRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);
        List<Long> universityScheduleIdList = request.getUniversityScheduleIdList();
        List<UniversitySchedule> universityScheduleList
                = universityScheduleRepository.findAllById(universityScheduleIdList);

        if (universityScheduleList.size() != universityScheduleIdList.size()) {
            throw new UniversityScheduleNotFoundException();
        }

        List<UserUniversitySchedule> userUniversityScheduleList = universityScheduleList.stream()
                .map(us -> UserUniversitySchedule.builder()
                        .user(user)
                        .universitySchedule(us)
                        .build())
                .toList();

        userUniversityScheduleRepository.saveAll(userUniversityScheduleList);
    }
}
