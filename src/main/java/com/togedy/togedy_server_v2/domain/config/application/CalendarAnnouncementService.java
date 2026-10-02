package com.togedy.togedy_server_v2.domain.config.application;

import com.togedy.togedy_server_v2.domain.config.dao.CalendarAnnouncementRepository;
import com.togedy.togedy_server_v2.domain.config.dto.CalendarAnnouncementDto;
import com.togedy.togedy_server_v2.domain.config.dto.CalendarAnnouncementRequest;
import com.togedy.togedy_server_v2.domain.config.dto.GetAnnouncementResponse;
import com.togedy.togedy_server_v2.domain.config.entity.CalendarAnnouncement;
import com.togedy.togedy_server_v2.domain.config.exception.CalendarAnnouncementNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarAnnouncementService {

    private final CalendarAnnouncementRepository calendarAnnouncementRepository;

    /**
     * 앱 캘린더에 노출할 공지를 조회한다.
     * <p>
     * 가장 최근에 등록된 공지 1건을 반환하며, 등록된 공지가 없으면 공지 없음 응답을 반환한다.
     * </p>
     *
     * @return 공지사항 정보 반환
     */
    public GetAnnouncementResponse findAnnouncement() {
        return calendarAnnouncementRepository.findTopByOrderByCreatedAtDesc()
                .map(calendarAnnouncement -> GetAnnouncementResponse.from(calendarAnnouncement.getContent()))
                .orElseGet(GetAnnouncementResponse::temp);
    }

    /**
     * 캘린더 공지 목록을 등록일 기준 내림차순으로 조회한다.
     *
     * @return 캘린더 공지 목록
     */
    public List<CalendarAnnouncementDto> findCalendarAnnouncements() {
        return calendarAnnouncementRepository.findAll(Sort.by(Direction.DESC, "createdAt"))
                .stream()
                .map(CalendarAnnouncementDto::from)
                .toList();
    }

    /**
     * 단일 캘린더 공지를 조회한다.
     *
     * @param calendarAnnouncementId 조회 대상 캘린더 공지 ID
     * @return 캘린더 공지
     * @throws CalendarAnnouncementNotFoundException 해당 캘린더 공지가 존재하지 않는 경우
     */
    public CalendarAnnouncementDto findCalendarAnnouncement(Long calendarAnnouncementId) {
        return CalendarAnnouncementDto.from(getCalendarAnnouncement(calendarAnnouncementId));
    }

    /**
     * 캘린더 공지를 생성한다.
     *
     * @param request 캘린더 공지 요청 DTO
     * @param userId  작성한 관리자 유저 ID
     */
    @Transactional
    public void generateCalendarAnnouncement(CalendarAnnouncementRequest request, Long userId) {
        CalendarAnnouncement calendarAnnouncement = CalendarAnnouncement.builder()
                .userId(userId)
                .content(request.getContent())
                .build();

        calendarAnnouncementRepository.save(calendarAnnouncement);
    }

    /**
     * 캘린더 공지를 수정한다.
     *
     * @param request                캘린더 공지 요청 DTO
     * @param calendarAnnouncementId 수정 대상 캘린더 공지 ID
     * @throws CalendarAnnouncementNotFoundException 해당 캘린더 공지가 존재하지 않는 경우
     */
    @Transactional
    public void modifyCalendarAnnouncement(CalendarAnnouncementRequest request, Long calendarAnnouncementId) {
        getCalendarAnnouncement(calendarAnnouncementId).update(request.getContent());
    }

    /**
     * 캘린더 공지를 삭제한다.
     *
     * @param calendarAnnouncementId 삭제 대상 캘린더 공지 ID
     * @throws CalendarAnnouncementNotFoundException 해당 캘린더 공지가 존재하지 않는 경우
     */
    @Transactional
    public void removeCalendarAnnouncement(Long calendarAnnouncementId) {
        calendarAnnouncementRepository.delete(getCalendarAnnouncement(calendarAnnouncementId));
    }

    private CalendarAnnouncement getCalendarAnnouncement(Long calendarAnnouncementId) {
        return calendarAnnouncementRepository.findById(calendarAnnouncementId)
                .orElseThrow(CalendarAnnouncementNotFoundException::new);
    }
}
