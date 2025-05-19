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
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
    public List<GetUniversityScheduleResponse> findUniversityScheduleList(
            String name,
            String admissionType,
            Long   userId)
    {
        if (StringUtils.hasText(admissionType)
                && !("수시".equals(admissionType) || "정시".equals(admissionType))) {
            throw new InvalidAdmissionTypeException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        List<AdmissionSchedule> schedules = admissionScheduleRepository
                .findAllWithUserFlag(userId, name, ACADEMIC_YEAR, admissionType);

        Set<Long> ownedIds = new HashSet<>
                (userUniversityScheduleRepository.findAddedUniversityScheduleIds(userId));

        return schedules.stream()
                .collect(Collectors.groupingBy(
                        as -> as.getAdmissionMethod().getUniversity(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(e -> toResponse(e.getKey(), e.getValue(), ownedIds)).toList();
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

    private GetUniversityScheduleResponse toResponse(
            University uni,
            List<AdmissionSchedule> list,
            Set<Long> ownedIds
    ) {
        List<AdmissionTypeDto> admissionTypes = buildAdmissionList(list);

        Set<Long> scheduleIds = list.stream()
                .map(as -> as.getUniversitySchedule().getId())
                .collect(Collectors.toSet());
        boolean isAdded = ownedIds.containsAll(scheduleIds);

        return GetUniversityScheduleResponse.of(uni, admissionTypes, isAdded);
    }

    private List<AdmissionTypeDto> buildAdmissionList(List<AdmissionSchedule> list) {
        return list.stream()
                .collect(Collectors.groupingBy(
                        as -> as.getAdmissionMethod().getName(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                AdmissionSchedule::getUniversitySchedule,
                                Collectors.toCollection(LinkedHashSet::new)
                        )
                ))
                .entrySet().stream()
                .map(entry -> AdmissionTypeDto.of(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(UniversityScheduleDto::from)
                                .toList()
                ))
                .toList();
    }
}
