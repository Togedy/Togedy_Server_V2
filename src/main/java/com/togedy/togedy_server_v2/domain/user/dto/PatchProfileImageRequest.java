package com.togedy.togedy_server_v2.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class PatchProfileImageRequest {

    private MultipartFile userProfileImage;

    private Boolean removeUserProfileImage;

}
