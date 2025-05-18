package com.togedy.togedy_server_v2.domain.university.application;

import com.togedy.togedy_server_v2.domain.university.dao.AdmissionScheduleRepository;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityScheduleRepository universityScheduleRepository;
    private final UserUniversityScheduleRepository userUniversityScheduleRepository;
    private final UserRepository userRepository;
    private final AdmissionScheduleRepository admissionScheduleRepository;

    private static final int ACADEMIC_YEAR = 2025;

    @Transactional(readOnly = true)
    public List<GetUniversityScheduleResponse> findUniversityScheduleList(String name, Long userId) {
        // 1) AdmissionSchedule + fetch join 한 번 호출
        List<AdmissionSchedule> schedules =
                admissionScheduleRepository.findAllWithUserFlag(userId, name, ACADEMIC_YEAR);

        // 2) 사용자가 추가한 스케줄 ID 세트 조회 (한 번 호출)
        Set<Long> addedIds = new HashSet<>(
                userUniversityScheduleRepository.findAddedUniversityScheduleIds(userId)
        );

        // 3) (대학 → AdmissionMethod 이름) 단위로 그룹핑
        return schedules.stream()
                .collect(Collectors.groupingBy(
                        asch -> asch.getAdmissionMethod().getUniversity(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(univEntry -> {
                    University uni = univEntry.getKey();
                    List<AdmissionSchedule> schedList = univEntry.getValue();

                    // 4) 전형별 Schedule DTO 묶기 (각 그룹 내에서 ID 기준 중복 제거)
                    List<AdmissionTypeDto> admissionList = schedList.stream()
                            .collect(Collectors.groupingBy(
                                    as -> as.getAdmissionMethod().getName(),
                                    LinkedHashMap::new,
                                    Collectors.mapping(
                                            AdmissionSchedule::getUniversitySchedule,
                                            Collectors.toList()
                                    )
                            ))
                            .entrySet().stream()
                            .map(admEntry -> {
                                String admissionName = admEntry.getKey();
                                List<UniversitySchedule> usList = admEntry.getValue();

                                // AdmissionMethod별로 중복된 스케줄(ID 동일)은 첫 건만 유지
                                List<UniversityScheduleDto> dtos = new ArrayList<>(
                                        usList.stream()
                                                .collect(Collectors.toMap(
                                                        UniversitySchedule::getId,
                                                        Function.identity(),
                                                        (first, second) -> first,
                                                        LinkedHashMap::new
                                                ))
                                                .values().stream()
                                                .map(UniversityScheduleDto::from)
                                                .toList()
                                );

                                return AdmissionTypeDto.of(admissionName, dtos);
                            })
                            .toList();

                    // 5) 이 대학(모든 전형)에 속한 스케줄 ID 집합을 따로 구해서 isAdded 계산
                    Set<Long> uniScheduleIds = schedList.stream()
                            .map(as -> as.getUniversitySchedule().getId())
                            .collect(Collectors.toSet());

                    boolean isAdded = uniScheduleIds.stream()
                            .allMatch(addedIds::contains);

                    return GetUniversityScheduleResponse.of(uni, admissionList, isAdded);
                })
                .toList();
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
