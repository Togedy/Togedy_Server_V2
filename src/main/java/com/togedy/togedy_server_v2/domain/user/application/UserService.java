package com.togedy.togedy_server_v2.domain.user.application;

import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.dto.CreateUserRequest;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.DuplicateEmailException;
import com.togedy.togedy_server_v2.domain.user.exception.DuplicateNicknameException;
import com.togedy.togedy_server_v2.domain.user.exception.UserNotFoundException;
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
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException();
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        User user = User.create(request.getNickname(), request.getEmail());
        return userRepository.save(user).getId();
    }

    public JwtTokenInfo signInUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        return jwtTokenProvider.generateTokenInfo(user.getId());
    }

    @Transactional(readOnly = true)
    public User loadUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
