package com.example.japanese.mapper;

import com.example.japanese.dto.response.UserResponse;
import com.example.japanese.entity.User;
import org.springframework.stereotype.Component;

/**
 * Manual entity/DTO mapper. Keeps JPA entities from ever being serialized
 * directly through the REST layer (see requirements section 2.2).
 */
@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .build();
    }
}
