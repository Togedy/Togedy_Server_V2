package com.togedy.togedy_server_v2.domain.badge.application;

import com.togedy.togedy_server_v2.domain.badge.dao.HolidayKindRepository;
import com.togedy.togedy_server_v2.domain.badge.dao.HolidayRepository;
import com.togedy.togedy_server_v2.domain.badge.dto.HolidayKindDto;
import com.togedy.togedy_server_v2.domain.badge.dto.HolidayKindRequest;
import com.togedy.togedy_server_v2.domain.badge.entity.HolidayKind;
import com.togedy.togedy_server_v2.domain.badge.event.BadgeImageRemovedEvent;
import com.togedy.togedy_server_v2.domain.badge.exception.BadgeImageRequiredException;
import com.togedy.togedy_server_v2.domain.badge.exception.DuplicateHolidayKindNameException;
import com.togedy.togedy_server_v2.domain.badge.exception.HolidayKindInUseException;
import com.togedy.togedy_server_v2.domain.badge.exception.HolidayKindNotFoundException;
import com.togedy.togedy_server_v2.global.enums.ImageCategory;
import com.togedy.togedy_server_v2.global.service.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayKindService {

    private final HolidayKindRepository holidayKindRepository;
    private final HolidayRepository holidayRepository;
    private final S3Service s3Service;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 연휴 종류 목록을 등록 순으로 조회한다.
     *
     * @return 연휴 종류 목록
     */
    public List<HolidayKindDto> findHolidayKinds() {
        return holidayKindRepository.findAll(Sort.by("id"))
                .stream()
                .map(HolidayKindDto::of)
                .toList();
    }

    /**
     * 단일 연휴 종류를 조회한다.
     *
     * @param holidayKindId 조회 대상 연휴 종류 ID
     * @return 연휴 종류
     * @throws HolidayKindNotFoundException 해당 연휴 종류가 존재하지 않는 경우
     */
    public HolidayKindDto findHolidayKind(Long holidayKindId) {
        return HolidayKindDto.of(getHolidayKind(holidayKindId));
    }

    /**
     * 연휴 종류를 등록한다.
     * <p>
     * 검증을 모두 통과한 뒤 뱃지 이미지를 업로드해, 검증 실패로 사용되지 않는 이미지가 남지 않도록 한다.
     * </p>
     *
     * @param request 연휴 종류 요청 DTO
     * @throws DuplicateHolidayKindNameException 같은 이름의 연휴 종류가 존재하는 경우
     * @throws BadgeImageRequiredException       뱃지를 지급하는데 뱃지 이미지가 없는 경우
     */
    @Transactional
    public void generateHolidayKind(HolidayKindRequest request) {
        if (holidayKindRepository.existsByName(request.getName())) {
            throw new DuplicateHolidayKindNameException();
        }
        validateBadgeImage(request, null);

        HolidayKind holidayKind = HolidayKind.builder()
                .name(request.getName())
                .badgeImageUrl(uploadBadgeImage(request))
                .badgeEnabled(request.isBadgeEnabled())
                .build();

        holidayKindRepository.save(holidayKind);
    }

    /**
     * 연휴 종류를 수정한다.
     * <p>
     * 새 뱃지 이미지가 없으면 기존 이미지를 유지하고, 새 이미지로 교체하면 커밋 후 기존 이미지를 삭제한다.
     * </p>
     *
     * @param request       연휴 종류 요청 DTO
     * @param holidayKindId 수정 대상 연휴 종류 ID
     * @throws HolidayKindNotFoundException      해당 연휴 종류가 존재하지 않는 경우
     * @throws DuplicateHolidayKindNameException 같은 이름의 연휴 종류가 존재하는 경우
     * @throws BadgeImageRequiredException       뱃지를 지급하는데 뱃지 이미지가 없는 경우
     */
    @Transactional
    public void modifyHolidayKind(HolidayKindRequest request, Long holidayKindId) {
        HolidayKind holidayKind = getHolidayKind(holidayKindId);
        if (holidayKindRepository.existsByNameAndIdNot(request.getName(), holidayKindId)) {
            throw new DuplicateHolidayKindNameException();
        }
        validateBadgeImage(request, holidayKind.getBadgeImageUrl());

        if (request.hasBadgeImage()) {
            String oldBadgeImageUrl = holidayKind.changeBadgeImageUrl(uploadBadgeImage(request));
            publishBadgeImageRemovedEvent(oldBadgeImageUrl);
        }
        holidayKind.update(request.getName(), request.isBadgeEnabled());
    }

    /**
     * 연휴 종류를 삭제한다.
     * <p>
     * 연휴가 등록된 종류는 지급된 연휴 파이터 뱃지의 이미지를 찾는 데 쓰이므로 삭제할 수 없다.
     * </p>
     *
     * @param holidayKindId 삭제 대상 연휴 종류 ID
     * @throws HolidayKindNotFoundException 해당 연휴 종류가 존재하지 않는 경우
     * @throws HolidayKindInUseException    연휴가 등록된 종류인 경우
     */
    @Transactional
    public void removeHolidayKind(Long holidayKindId) {
        HolidayKind holidayKind = getHolidayKind(holidayKindId);
        if (holidayRepository.existsByHolidayKindId(holidayKindId)) {
            throw new HolidayKindInUseException();
        }

        holidayKindRepository.delete(holidayKind);
        publishBadgeImageRemovedEvent(holidayKind.getBadgeImageUrl());
    }

    /**
     * 뱃지를 지급하는 연휴 종류에 뱃지 이미지가 있는지 검증한다.
     *
     * @param request          연휴 종류 요청 DTO
     * @param badgeImageUrl    기존 뱃지 이미지 URL, 등록 시 {@code null}
     * @throws BadgeImageRequiredException 뱃지를 지급하는데 새 이미지와 기존 이미지가 모두 없는 경우
     */
    private void validateBadgeImage(HolidayKindRequest request, String badgeImageUrl) {
        if (request.isBadgeEnabled() && !request.hasBadgeImage() && badgeImageUrl == null) {
            throw new BadgeImageRequiredException();
        }
    }

    /**
     * 요청에 뱃지 이미지가 있으면 S3에 업로드한다.
     *
     * @param request 연휴 종류 요청 DTO
     * @return 업로드한 이미지 URL, 이미지가 없으면 {@code null}
     */
    private String uploadBadgeImage(HolidayKindRequest request) {
        if (!request.hasBadgeImage()) {
            return null;
        }
        return s3Service.uploadFile(request.getBadgeImage(), ImageCategory.BADGE);
    }

    /**
     * 기존 뱃지 이미지 삭제 이벤트를 발행한다.
     *
     * @param badgeImageUrl 삭제 대상 이미지 URL
     */
    private void publishBadgeImageRemovedEvent(String badgeImageUrl) {
        if (badgeImageUrl == null || badgeImageUrl.isBlank()) {
            return;
        }

        applicationEventPublisher.publishEvent(new BadgeImageRemovedEvent(badgeImageUrl));
    }

    /**
     * 연휴 종류를 조회한다.
     *
     * @param holidayKindId 조회 대상 연휴 종류 ID
     * @return 연휴 종류
     * @throws HolidayKindNotFoundException 해당 연휴 종류가 존재하지 않는 경우
     */
    private HolidayKind getHolidayKind(Long holidayKindId) {
        return holidayKindRepository.findById(holidayKindId)
                .orElseThrow(HolidayKindNotFoundException::new);
    }
}
