package com.togedy.togedy_server_v2.domain.config.application;

import com.togedy.togedy_server_v2.domain.config.dao.ConfigRepository;
import com.togedy.togedy_server_v2.domain.config.dto.GetAnnouncementResponse;
import com.togedy.togedy_server_v2.domain.config.entity.AppConfig;
import com.togedy.togedy_server_v2.domain.config.exception.AppConfigNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;
    private final static String CONFIG_KEY = "latest_version";

    /**
     * 공지사항을 조회한다.
     *
     * @return  공지사항 정보 반환
     */
    public GetAnnouncementResponse findAnnouncement() {
        AppConfig appConfig = configRepository.findById(CONFIG_KEY)
                .orElseThrow(AppConfigNotFoundException::new);

        String announcement = appConfig.getAnnouncement();

        if (announcement == null || announcement.isBlank()) {
            return GetAnnouncementResponse.temp();
        }

        return GetAnnouncementResponse.from(appConfig.getAnnouncement());
    }
}
