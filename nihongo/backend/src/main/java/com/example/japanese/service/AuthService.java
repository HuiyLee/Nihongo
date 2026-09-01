package com.example.japanese.service;

import com.example.japanese.dto.request.LoginRequest;
import com.example.japanese.dto.request.RegisterRequest;
import com.example.japanese.dto.response.LoginResponse;
import com.example.japanese.dto.response.UserResponse;
import com.example.japanese.entity.RefreshToken;
import com.example.japanese.entity.Role;
import com.example.japanese.entity.User;
import com.example.japanese.exception.DuplicateResourceException;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.exception.UnauthorizedException;
import com.example.japanese.mapper.UserMapper;
import com.example.japanese.repository.RefreshTokenRepository;
import com.example.japanese.repository.RoleRepository;
import com.example.japanese.repository.UserRepository;
import com.example.japanese.security.JwtService;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.util.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final com.example.japanese.config.JwtProperties jwtProperties;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Role userRole = roleRepository.findByName(Role.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found: " + Role.USER));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(userRole)
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return issueTokens(user);
    }

    @Transactional
    public LoginResponse refresh(String rawRefreshToken) {
        RefreshToken existing = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (existing.isRevoked() || existing.isExpired()) {
            throw new UnauthorizedException("Refresh token is expired or has been revoked");
        }

        // Rotate: revoke the used token and issue a brand new pair.
        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        return issueTokens(existing.getUser());
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenRepository.findByToken(rawRefreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    private LoginResponse issueTokens(User user) {
        UserPrincipal principal = new UserPrincipal(user);
        String accessToken = jwtService.generateAccessToken(principal);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(TokenGenerator.generate())
                .expiresAt(LocalDateTime.now().plusNanos(jwtProperties.getRefreshTokenExpirationMs() * 1_000_000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(userMapper.toResponse(user))
                .build();
    }
}
