package com.togedy.togedy_server_v2.domain.user.application;

import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.dto.CreateUserRequest;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.UserException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;
import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenInfo;
import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Long generateUser(CreateUserRequest request) {
        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserException(ErrorCode.DUPLICATED_NICKNAME);
        }
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException(ErrorCode.DUPLICATED_EMAIL);
        }

        User user = request.toEntity();
        return userRepository.save(user).getId();
    }

    public JwtTokenInfo signInUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return jwtTokenProvider.generateTokenInfo(user.getId(), user.getEmail());
    }
}
