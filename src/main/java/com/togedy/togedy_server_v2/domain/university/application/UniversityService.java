package com.togedy.togedy_server_v2.domain.university.application;

import com.togedy.togedy_server_v2.domain.university.dao.UniversityAdmissionMethodRepository;
import com.togedy.togedy_server_v2.domain.university.dao.UniversityRepository;
import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityMethodRepository;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityScheduleResponse;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityResponse;
import com.togedy.togedy_server_v2.domain.university.dao.UniversityScheduleRepository;
import com.togedy.togedy_server_v2.domain.university.dto.PostUniversityAdmissionMethodRequest;
import com.togedy.togedy_server_v2.domain.university.dto.UniversityAdmissionMethodDto;
import com.togedy.togedy_server_v2.domain.university.dto.UniversityScheduleDto;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UserUniversityMethod;
import com.togedy.togedy_server_v2.domain.university.exception.UniversityAdmissionMethodNotFoundException;
import com.togedy.togedy_server_v2.domain.university.exception.UniversityNotFoundException;
import com.togedy.togedy_server_v2.domain.user.application.UserService;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityScheduleRepository universityScheduleRepository;
    private final UniversityAdmissionMethodRepository universityAdmissionMethodRepository;
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final UserUniversityMethodRepository userUniversityMethodRepository;
    private final UserService userService;

    private static final List<String> STAGE_ORDER = List.of(
            "원서접수", "서류제출", "합격발표"
    );

    private static final int ACADEMIC_YEAR = 2025;

    /***
     * 대학명, 입시 전형에 해당하는 대학 정보를 조회한다.
     *
     * @param name              대학명
     * @param admissionType     입시 전형(수시, 정시)
     * @param userId            유저ID
     * @param page              페이지
     * @param size              크기
     * @return                  대학별 정보
     */
    public List<GetUniversityResponse> findUniversityList(
            String name,
            String admissionType,
            Long userId,
            int page,
            int size
    ) {
        User user = userService.loadUserById(userId);

        if ("전체".equals(admissionType)) {
            admissionType = null;
        }

        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.by("name"));
        List<University> universityList = universityRepository.findByNameAndType(name, admissionType, pageRequest);
        List<Long> universityIdList = universityList.stream()
                .map(University::getId)
                .toList();

        Map<Long, Long> universityAdmissionCountMap = universityAdmissionMethodRepository
                .findCountByUniversityIds(universityIdList)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        Map<Long, List<UniversityAdmissionMethod>> addedAdmissionMethodMap =
                universityAdmissionMethodRepository
                        .findAllByUniversityIdsAndUserId(universityIdList, userId)
                        .stream()
                        .collect(Collectors.groupingBy(m -> m.getUniversity().getId()));

        return universityList.stream()
                .map(university -> GetUniversityResponse.of(
                       university,
                       universityAdmissionCountMap.getOrDefault(university.getId(), 0L).intValue(),
                       addedAdmissionMethodMap.getOrDefault(university.getId(), Collections.emptyList())
               )).toList();
    }

    /***
     * 해당 대학의 전형별 일정을 조회한다.
     *
     * @param universityId  대학ID
     * @param userId        유저ID
     * @return              해당 대학의 전형별 일정
     */
    public GetUniversityScheduleResponse findUniversitySchedule(Long universityId, Long userId) {
        User user = userService.loadUserById(userId);

        University university = universityRepository.findById(universityId)
                .orElseThrow(UniversityNotFoundException::new);

        List<UniversityAdmissionMethod> addedUniversityAdmissionMethodList =
                universityAdmissionMethodRepository.findAllByUniversityAndUserId(university, userId);

        List<UniversityAdmissionMethodDto> universityAdmissionMethodDtoList = universityAdmissionMethodRepository
                .findAllByUniversity(university)
                .stream()
                .map(method -> {
                    List<UniversityScheduleDto> scheduleDtos = method
                            .getUniversityAdmissionScheduleList()
                            .stream()
                            .map(uas -> UniversityScheduleDto.from(uas.getUniversitySchedule()))
                            .sorted(Comparator.comparingInt(
                                    dto -> STAGE_ORDER.indexOf(dto.getUniversityAdmissionStage())
                            ))
                            .collect(Collectors.toList());
                    return UniversityAdmissionMethodDto.of(method, scheduleDtos);
                })
                .toList();

        return GetUniversityScheduleResponse.of(university, addedUniversityAdmissionMethodList, universityAdmissionMethodDtoList);
    }

    /***
     * 유저가 대학 전형들을 추가한다.
     *
     * @param request   대학 전형ID 리스트
     * @param userId    유저ID
     */
    @Transactional
    public void generateUserUniversityAdmissionMethod(PostUniversityAdmissionMethodRequest request, Long userId) {
        User user = userService.loadUserById(userId);

        List<Long> universityAdmissionMethodIdList = request.getUniversityAdmissionMethodIdList();

        List<UniversityAdmissionMethod> universityAdmissionMethodList
                = universityAdmissionMethodRepository.findAllById(universityAdmissionMethodIdList);

        if (universityAdmissionMethodList.size() != universityAdmissionMethodIdList.size()) {
            throw new UniversityAdmissionMethodNotFoundException();
        }

        List<UserUniversityMethod> userUniversityMethodList = universityAdmissionMethodList.stream()
                .map(us -> UserUniversityMethod.builder()
                        .user(user)
                        .universityAdmissionMethod(us)
                        .build())
                .toList();

        userUniversityMethodRepository.saveAll(userUniversityMethodList);
    }

    /***
     * 유저가 보유한 대학 전형을 제거한다.
     *
     * @param universityAdmissionMethodIdList  대학 전형ID 리스트
     * @param userId                           유저ID
     */
    @Transactional
    public void removeUserUniversityMethod(List<Long> universityAdmissionMethodIdList, Long userId) {
        User user = userService.loadUserById(userId);

        List<UserUniversityMethod> userUniversityMethodList =
                userUniversityMethodRepository
                        .findByUserIdAndUniversityAdmissionMethodIdIn(userId, universityAdmissionMethodIdList);

        if (userUniversityMethodList.size() != universityAdmissionMethodIdList.size()) {
            throw new UniversityAdmissionMethodNotFoundException();
        }

        userUniversityMethodRepository.deleteAll(userUniversityMethodList);
    }

}
