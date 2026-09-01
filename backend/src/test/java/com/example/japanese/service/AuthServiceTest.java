package com.example.japanese.service;

import com.example.japanese.config.JwtProperties;
import com.example.japanese.dto.request.RegisterRequest;
import com.example.japanese.dto.response.UserResponse;
import com.example.japanese.entity.Role;
import com.example.japanese.entity.User;
import com.example.japanese.exception.DuplicateResourceException;
import com.example.japanese.mapper.UserMapper;
import com.example.japanese.repository.RefreshTokenRepository;
import com.example.japanese.repository.RoleRepository;
import com.example.japanese.repository.UserRepository;
import com.example.japanese.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserMapper userMapper;

    private JwtProperties jwtProperties;

    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setAccessTokenExpirationMs(900_000L);
        jwtProperties.setRefreshTokenExpirationMs(604_800_000L);
        jwtProperties.setSecret("dGVzdC1vbmx5LXNlY3JldC1rZXktZm9yLXVuaXQtdGVzdHMtMTIzNDU2Nzg=");

        authService = new AuthService(
                userRepository, roleRepository, refreshTokenRepository,
                passwordEncoder, authenticationManager, jwtService, userMapper, jwtProperties
        );

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("user01");
        registerRequest.setEmail("user01@example.com");
        registerRequest.setPassword("Password1");
        registerRequest.setFullName("User One");
    }

    @Test
    void register_throwsDuplicateResourceException_whenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("user01")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Username");
    }

    @Test
    void register_throwsDuplicateResourceException_whenEmailAlreadyExists() {
        when(userRepository.existsByUsername("user01")).thenReturn(false);
        when(userRepository.existsByEmail("user01@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void register_savesUserWithHashedPassword_whenDataIsValid() {
        when(userRepository.existsByUsername("user01")).thenReturn(false);
        when(userRepository.existsByEmail("user01@example.com")).thenReturn(false);
        when(roleRepository.findByName(Role.USER))
                .thenReturn(java.util.Optional.of(Role.builder().name(Role.USER).build()));
        when(passwordEncoder.encode("Password1")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenReturn(
                UserResponse.builder().id(1L).username("user01").email("user01@example.com").role(Role.USER).build()
        );

        UserResponse response = authService.register(registerRequest);

        assertThat(response.getUsername()).isEqualTo("user01");
        assertThat(response.getRole()).isEqualTo(Role.USER);
    }
}
