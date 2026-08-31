package com.example.japanese.service;

import com.example.japanese.dto.response.UserResponse;
import com.example.japanese.entity.User;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.mapper.UserMapper;
import com.example.japanese.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }
}
