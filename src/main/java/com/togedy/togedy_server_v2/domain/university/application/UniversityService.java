package com.togedy.togedy_server_v2.domain.university.application;

import com.togedy.togedy_server_v2.domain.university.dao.UniversityAdmissionMethodRepository;
import com.togedy.togedy_server_v2.domain.university.dao.UniversityRepository;
import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityMethodRepository;
import com.togedy.togedy_server_v2.domain.university.dto.request.PostUniversityAdmissionMethodRequest;
import com.togedy.togedy_server_v2.domain.university.dto.response.GetUniversityResponse;
import com.togedy.togedy_server_v2.domain.university.dto.response.GetUniversityScheduleResponse;
import com.togedy.togedy_server_v2.domain.university.dto.response.UniversityAdmissionMethodInfo;
import com.togedy.togedy_server_v2.domain.university.dto.response.UniversityInfo;
import com.togedy.togedy_server_v2.domain.university.dto.response.UniversityScheduleInfo;
import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import com.togedy.togedy_server_v2.domain.university.entity.UserUniversityMethod;
import com.togedy.togedy_server_v2.domain.university.enums.AdmissionStage;
import com.togedy.togedy_server_v2.domain.university.enums.AdmissionType;
import com.togedy.togedy_server_v2.domain.university.exception.DuplicateUniversityAdmissionMethodException;
import com.togedy.togedy_server_v2.domain.university.exception.UniversityAdmissionMethodNotFoundException;
import com.togedy.togedy_server_v2.domain.university.exception.UniversityNotFoundException;
import com.togedy.togedy_server_v2.domain.university.exception.UserUniversityMethodNotOwnedException;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.user.UserNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityAdmissionMethodRepository universityAdmissionMethodRepository;
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final UserUniversityMethodRepository userUniversityMethodRepository;

    private static final int ACADEMIC_YEAR = 2026;

    /**
     * 대학명, 입시 전형에 해당하는 대학 정보를 페이지 단위로 조회한다.
     *
     * @param name          대학명 (검색어)
     * @param admissionType 입시 전형(수시, 정시, 전체). {@code null}이거나 전체인 경우 입시 전형 구분 없이 조회
     * @param userId        유저ID
     * @param page          페이지 번호 (1부터 시작)
     * @param size          페이지당 조회 개수
     * @return 다음 페이지 존재 여부와 대학별 정보 리스트
     */
    public GetUniversityResponse findUniversityList(
            String name,
            AdmissionType admissionType,
            Long userId,
            int page,
            int size
    ) {
        Slice<University> universities = searchUniversity(name, admissionType, page, size);
        List<Long> universityIds = getUniversityIds(universities);

        List<UniversityInfo> universityInfos = buildUniversityDto(
                universities,
                countAdmissionMethodByUniversity(universityIds),
                findAddedAdmissionMethods(userId, universityIds)
        );

        return GetUniversityResponse.of(universities.hasNext(), universityInfos);
    }

    /**
     * 해당 대학의 정보와 유저가 추가한 전형 목록, 전형별 일정을 조회한다.
     *
     * @param universityId 대학ID
     * @param userId       유저ID
     * @return 대학 정보, 유저가 추가한 전형 목록 및 전형별 일정
     * @throws UniversityNotFoundException 해당 대학이 존재하지 않는 경우
     */
    public GetUniversityScheduleResponse findUniversitySchedule(Long universityId, Long userId) {
        University university = findUniversityById(universityId);

        return GetUniversityScheduleResponse.of(
                university,
                findAddedAdmissionMethod(userId, university),
                buildUniversityAdmissionMethodDto(university)
        );
    }

    /**
     * 유저가 대학 전형을 추가한다.
     *
     * @param request 대학 전형 추가 DTO
     * @param userId  유저ID
     * @throws UserNotFoundException                       해당 유저가 존재하지 않는 경우
     * @throws UniversityAdmissionMethodNotFoundException  해당 대학 전형이 존재하지 않는 경우
     * @throws DuplicateUniversityAdmissionMethodException 해당 유저가 동일 전형을 이미 추가한 경우
     */
    @Transactional
    public void generateUserUniversityAdmissionMethod(PostUniversityAdmissionMethodRequest request, Long userId) {
        User user = findUserById(userId);
        UniversityAdmissionMethod admissionMethod = findAdmissionMethodById(request.getUniversityAdmissionMethodId());
        validateDuplicateAdmissionMethod(userId, request.getUniversityAdmissionMethodId());

        UserUniversityMethod userUniversityMethod = UserUniversityMethod.builder()
                .user(user)
                .universityAdmissionMethod(admissionMethod)
                .build();

        userUniversityMethodRepository.save(userUniversityMethod);
    }

    /**
     * 유저가 보유한 대학 전형을 제거한다.
     *
     * @param admissionMethodId 대학 전형ID
     * @param userId            유저ID
     * @throws UserUniversityMethodNotOwnedException 해당 유저가 추가하지 않은(소유하지 않은) 대학 전형인 경우
     */
    @Transactional
    public void removeUserUniversityMethod(Long admissionMethodId, Long userId) {
        UserUniversityMethod userUniversityMethod = userUniversityMethodRepository
                .findAddedUserUniversityMethod(admissionMethodId, userId)
                .orElseThrow(UserUniversityMethodNotOwnedException::new);

        userUniversityMethodRepository.delete(userUniversityMethod);
    }

    /**
     * 대학별 입시 전형의 개수를 집계한다.
     *
     * @param universityIds 대학 ID 리스트
     * @return 대학별 입시 전형 개수
     */
    private Map<Long, Long> countAdmissionMethodByUniversity(List<Long> universityIds) {
        return universityAdmissionMethodRepository
                .findCountByUniversityIdsAndAcademicYear(universityIds, ACADEMIC_YEAR)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    /**
     * 유저가 추가한 입시 전형을 대학별로 조회한다.
     *
     * @param userId        유저 ID
     * @param universityIds 대학 ID 리스트
     * @return 대학별 유저가 추가한 입시 전형 리스트
     */
    private Map<Long, List<UniversityAdmissionMethod>> findAddedAdmissionMethods(
            Long userId,
            List<Long> universityIds
    ) {
        return universityAdmissionMethodRepository
                .findAllByUniversityIdsAndUserIdAndAcademicYear(universityIds, userId, ACADEMIC_YEAR)
                .stream()
                .collect(Collectors.groupingBy(m -> m.getUniversity().getId()));
    }

    /**
     * 해당 대학의 입시 전형 목록을 조회하고, 각 전형에 속한 일정을 진행 단계({@link AdmissionStage#getOrder()} 기준) 순으로 정렬한다.
     *
     * @param university 대학
     * @return 전형별 일정이 단계 순으로 정렬된 대학 입시 전형 정보 리스트
     */
    private List<UniversityAdmissionMethodInfo> buildUniversityAdmissionMethodDto(University university) {
        return universityAdmissionMethodRepository
                .findAllByUniversityAndAcademicYear(university, ACADEMIC_YEAR)
                .stream()
                .map(method -> {
                    List<UniversityScheduleInfo> scheduleDtos = method.getUniversityAdmissionScheduleList()
                            .stream()
                            .map(uas -> UniversityScheduleInfo.from(uas.getUniversitySchedule()))
                            .sorted(Comparator.comparingInt(
                                    dto -> dto.getUniversityAdmissionStage().getOrder())
                            ).collect(Collectors.toList());
                    return UniversityAdmissionMethodInfo.of(method, scheduleDtos);
                }).toList();
    }

    /**
     * 해당 대학에 대해 유저가 추가한 입시 전형을 조회한다.
     *
     * @param userId     유저 ID
     * @param university 대학
     * @return 유저가 추가한 입시 전형 리스트
     */
    private List<UniversityAdmissionMethod> findAddedAdmissionMethod(
            Long userId,
            University university
    ) {
        return universityAdmissionMethodRepository.findAllByUniversityAndUserIdAndAcademicYear(
                university,
                userId,
                ACADEMIC_YEAR
        );
    }

    /**
     * 대학별 입시 전형 총 개수 및 유저가 추가한 입시 전형의 개수를 반환한다.
     *
     * @param universities            대학 리스트
     * @param admissionMethodCountMap 대학별 입시 전형 개수
     * @param addedAdmissionMethodMap 대학별 유저가 추가한 입시 전형 개수
     * @return 대학별 입시 전형 총 개수 및 유저가 추가한 입시 전형의 개수 정보
     */
    private List<UniversityInfo> buildUniversityDto(
            Slice<University> universities,
            Map<Long, Long> admissionMethodCountMap,
            Map<Long, List<UniversityAdmissionMethod>> addedAdmissionMethodMap
    ) {
        return universities.stream()
                .map(university -> UniversityInfo.of(
                        university,
                        admissionMethodCountMap.getOrDefault(university.getId(), 0L).intValue(),
                        addedAdmissionMethodMap.getOrDefault(university.getId(), Collections.emptyList())
                )).toList();
    }

    /**
     * 동일 입시 전형 추가를 검증한다.
     *
     * @param userId                      유저 ID
     * @param universityAdmissionMethodId 대학 입시 전형 ID
     * @throws DuplicateUniversityAdmissionMethodException 해당 유저가 동일 입시 전형을 추가하는 경우
     */
    private void validateDuplicateAdmissionMethod(Long userId, Long universityAdmissionMethodId) {
        if (userUniversityMethodRepository.existsByUniversityAdmissionMethodIdAndUserId(
                universityAdmissionMethodId,
                userId)
        ) {
            throw new DuplicateUniversityAdmissionMethodException();
        }
    }

    /**
     * 대학교를 조회한다.
     *
     * @param universityId 대학 ID
     * @return 대학교
     */
    private University findUniversityById(Long universityId) {
        return universityRepository.findById(universityId)
                .orElseThrow(UniversityNotFoundException::new);
    }

    /**
     * 각 대학교의 ID를 리스트로 반환한다.
     *
     * @param universities 대학교 리스트
     * @return 대학교 ID 리스트
     */
    private List<Long> getUniversityIds(Slice<University> universities) {
        return universities.stream()
                .map(University::getId)
                .toList();
    }

    /**
     * 대학 입시 전형을 조회한다.
     *
     * @param admissionMethodId 대학 입시 전형 ID
     * @return 대학 입시 전형
     * @throws UniversityAdmissionMethodNotFoundException 해당 대학 입시 전형이 존재하지 않는 경우
     */
    private UniversityAdmissionMethod findAdmissionMethodById(Long admissionMethodId) {
        return universityAdmissionMethodRepository.findById(admissionMethodId)
                .orElseThrow(UniversityAdmissionMethodNotFoundException::new);
    }

    /**
     * 대학명과 입시 전형 조건에 맞는 대학을 이름 순으로 정렬하여 페이지 단위로 조회한다.
     *
     * @param name          대학명 (검색어)
     * @param admissionType 입시 전형(수시, 정시, 전체). {@code null}이거나 전체인 경우 입시 전형 구분 없이 조회
     * @param page          페이지 번호 (1부터 시작, 0 이하로 들어와도 첫 페이지로 보정)
     * @param size          페이지당 조회 개수
     * @return 조건에 맞는 대학 Slice
     */
    private Slice<University> searchUniversity(String name, AdmissionType admissionType, int page, int size) {
        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.by("name"));
        if (AdmissionType.전체.equals(admissionType) || admissionType == null) {
            return universityRepository.findAllByName(name, pageRequest);
        }

        return universityRepository.findAllByNameAndAdmissionType(name, admissionType, pageRequest);
    }

    /**
     * 유저를 조회한다.
     *
     * @param userId 유저 ID
     * @return 유저
     * @throws UserNotFoundException 해당 유저가 존재하지 않는 경우
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
