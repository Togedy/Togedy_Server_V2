package com.togedy.togedy_server_v2.domain.badge.application;

import com.togedy.togedy_server_v2.domain.badge.dao.HolidayKindRepository;
import com.togedy.togedy_server_v2.domain.badge.dto.HolidayKindDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayKindService {

    private final HolidayKindRepository holidayKindRepository;

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
}
