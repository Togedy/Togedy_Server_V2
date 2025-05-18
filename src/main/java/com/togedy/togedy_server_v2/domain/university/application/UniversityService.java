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
import com.togedy.togedy_server_v2.domain.university.exception.InvalidAdmissionTypeException;
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
    public List<GetUniversityScheduleResponse> findUniversityScheduleList(String name, String admissionType, Long userId) {

        if (!admissionType.isBlank() || admissionType.equals("수시") || !admissionType.equals("정시")) {
            throw new InvalidAdmissionTypeException();
        }

        List<AdmissionSchedule> schedules =
                admissionScheduleRepository.findAllWithUserFlag(userId, name, ACADEMIC_YEAR, admissionType);

        Set<Long> ownedUniversityScheduleIds = new HashSet<>(
                userUniversityScheduleRepository.findAddedUniversityScheduleIds(userId)
        );

        return schedules.stream()
                .collect(Collectors.groupingBy(
                        admissionSchedule -> admissionSchedule.getAdmissionMethod().getUniversity(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(univEntry -> {
                    University uni = univEntry.getKey();
                    List<AdmissionSchedule> schedList = univEntry.getValue();

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

                                List<UniversityScheduleDto> universityScheduleDtos = new ArrayList<>(
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

                                return AdmissionTypeDto.of(admissionName, universityScheduleDtos);
                            })
                            .toList();

                    Set<Long> uniScheduleIds = schedList.stream()
                            .map(as -> as.getUniversitySchedule().getId())
                            .collect(Collectors.toSet());

                    boolean isAdded = ownedUniversityScheduleIds.containsAll(uniScheduleIds);

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
