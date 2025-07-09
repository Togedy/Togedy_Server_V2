package com.togedy.togedy_server_v2.domain.university.application;

import com.togedy.togedy_server_v2.domain.university.dao.AdmissionScheduleRepository;
import com.togedy.togedy_server_v2.domain.university.dao.UniversityRepository;
import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityScheduleRepository;
import com.togedy.togedy_server_v2.domain.university.dto.AdmissionTypeDto;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityScheduleResponse;
import com.togedy.togedy_server_v2.domain.university.dao.UniversityScheduleRepository;
import com.togedy.togedy_server_v2.domain.university.dto.PostUniversityScheduleRequest;
import com.togedy.togedy_server_v2.domain.university.dto.UniversityScheduleDto;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionSchedule;
import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversitySchedule;
import com.togedy.togedy_server_v2.domain.university.entity.UserUniversitySchedule;
import com.togedy.togedy_server_v2.domain.university.exception.InvalidAdmissionTypeException;
import com.togedy.togedy_server_v2.domain.university.exception.UniversityScheduleNotFoundException;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
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
    private final UniversityRepository universityRepository;

    private static final int ACADEMIC_YEAR = 2025;

    /**
     * name을 포함하는 대학 일정을 조회한다. admissionType을 통해 수시 혹은 정시 일정을 필터링한다.
     *
     * @param name              조회하고자 하는 대학 일정
     * @param admissionType     수시 혹은 정시 필터링
     * @param userId            유저ID
     * @return                  대학 일정 DTO
     */
    @Transactional(readOnly = true)
    public Page<GetUniversityScheduleResponse> findUniversityScheduleList(
            String name,
            String admissionType,
            Long   userId,
            int page,
            int size
    )
    {
        if (StringUtils.hasText(admissionType)
                && !("수시".equals(admissionType) || "정시".equals(admissionType))) {
            throw new InvalidAdmissionTypeException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<University> universities = universityRepository.findByNameAndType(name, admissionType, pageRequest);

        Set<Long> ownedIds = new HashSet<>
                (userUniversityScheduleRepository.findAddedUniversityScheduleIds(userId));

        List<String> stageOrder = List.of("원서접수", "서류제출", "합격발표");

        return universities.map(u -> {
            List<UniversityAdmissionSchedule> schedules = admissionScheduleRepository
                    .findByUniversityAndYear(u.getId(), ACADEMIC_YEAR);
            schedules.sort(Comparator.comparingInt(s -> stageOrder.indexOf(s.getUniversitySchedule().getAdmissionStage())));
            return toResponse(u, schedules, ownedIds);
        });
    }

    /**
     * 대학 일정을 유저의 일정으로 추가한다.
     *
     * @param request   추가할 대학 일정 ID List
     * @param userId    유저ID
     */
    @Transactional
    public void generateUserUniversitySchedule(PostUniversityScheduleRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
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

    /**
     * 유저가 보유 중인 대학 일정을 제거한다.
     *
     * @param universityScheduleIdList  제거할 대학 일정 ID List
     * @param userId                    유저ID
     */
    @Transactional
    public void removeUserUniversitySchedule(List<Long> universityScheduleIdList, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<UserUniversitySchedule> userUniversityScheduleList =
                userUniversityScheduleRepository
                        .findByUserAndUniversityScheduleIdIn(user, universityScheduleIdList);

        if (userUniversityScheduleList.isEmpty()) {
            throw new UniversityScheduleNotFoundException();
        }

        userUniversityScheduleRepository.deleteAll(userUniversityScheduleList);
    }

    /**
     * 대학 일정 조회 응답을 생성한다. 유저의 해당 대학 일정 보유 여부를 포함한다.
     *
     * @param university            University 객체
     * @param universityAdmissionSchedules    AdmissionSchedule 객체 List
     * @param ownedIds              유저가 보유 중인 대학ID Set
     * @return                      대학 일정 조회 DTO
     */
    private GetUniversityScheduleResponse toResponse(
            University university,
            List<UniversityAdmissionSchedule> universityAdmissionSchedules,
            Set<Long> ownedIds
    ) {
        List<AdmissionTypeDto> admissionTypes = buildAdmissionList(universityAdmissionSchedules);

        Set<Long> scheduleIds = universityAdmissionSchedules.stream()
                .map(as -> as.getUniversitySchedule().getId())
                .collect(Collectors.toSet());
        boolean isAdded = ownedIds.containsAll(scheduleIds);

        return GetUniversityScheduleResponse.of(university, admissionTypes, isAdded);
    }

    /**
     * 전형 별로 DTO를 생성한다.
     *
     * @param universityAdmissionScheduleList AdmissionSchedule 객체 List
     * @return                      전형 별로 생성된 대학 일정 DTO List
     */
    private List<AdmissionTypeDto> buildAdmissionList(List<UniversityAdmissionSchedule> universityAdmissionScheduleList) {
        return universityAdmissionScheduleList.stream()
                .collect(Collectors.groupingBy(
                        as -> as.getUniversityAdmissionMethod().getName(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                UniversityAdmissionSchedule::getUniversitySchedule,
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
