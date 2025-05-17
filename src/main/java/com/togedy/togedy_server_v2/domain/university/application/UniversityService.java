package com.togedy.togedy_server_v2.domain.university.application;

import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityScheduleRepository;
import com.togedy.togedy_server_v2.domain.university.dto.AdmissionTypeDto;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityScheduleResponse;
import com.togedy.togedy_server_v2.domain.university.dao.UniversityScheduleRepository;
import com.togedy.togedy_server_v2.domain.university.dto.PostUniversityScheduleRequest;
import com.togedy.togedy_server_v2.domain.university.dto.UniversityScheduleDto;
import com.togedy.togedy_server_v2.domain.university.entity.AdmissionSchedule;
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

    private static final int ACADEMIC_YEAR = 2025;

    public List<GetUniversityScheduleResponse> findUniversityScheduleList(String name) {
        List<UniversitySchedule> universitiScheduleList =
                universityScheduleRepository.findByUniversityNameLikeAndYear(name, ACADEMIC_YEAR);

        List<AdmissionSchedule> admissionScheduleList = universitiScheduleList.stream()
                .flatMap(us -> us.getAdmissionScheduleList().stream())
                .toList();

        Map<University, List<AdmissionSchedule>> universityListMap = admissionScheduleList.stream()
                .collect(Collectors.groupingBy(
                        admissionSchedule -> admissionSchedule.getAdmissionMethod().getUniversity(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<GetUniversityScheduleResponse> response = new ArrayList<>();
        universityListMap.forEach((uni, scheduleList) -> {
            List<AdmissionTypeDto> admissionList = scheduleList.stream()
                    .collect(Collectors.groupingBy(
                            schedule -> schedule.getAdmissionMethod().getName(),      // key: 전형 이름
                            LinkedHashMap::new,
                            Collectors.mapping(
                                    schedule -> UniversityScheduleDto.from(schedule.getUniversitySchedule()),  // value: 스케줄 DTO
                                    Collectors.toList()
                            )
                    ))
                    .entrySet().stream()
                    .map(e -> AdmissionTypeDto.of(e.getKey(), e.getValue()))
                    .toList();

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

    @Transactional
    public void removeUserUniversitySchedule(List<Long> universityScheduleIdList, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        List<UserUniversitySchedule> userUniversityScheduleList =
                userUniversityScheduleRepository
                        .findByUserAndUniversityScheduleIdIn(user, universityScheduleIdList);

        if (userUniversityScheduleList.isEmpty()) {
            throw new UniversityScheduleNotFoundException();
        }

        userUniversityScheduleRepository.deleteAll(userUniversityScheduleList);
    }
}
