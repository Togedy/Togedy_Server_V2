package com.togedy.togedy_server_v2.domain.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchProfileRequest {

    private String nickname;

    private MultipartFile userProfileImage;

    private boolean removeUserProfileImage;

}
