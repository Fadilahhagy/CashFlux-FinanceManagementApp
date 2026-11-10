package com.cashflux.user.service;

import com.cashflux.user.dto.RegisterRequest;
import com.cashflux.user.dto.UserResponse;
import com.cashflux.user.entity.User;
import com.cashflux.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;
import com.cashflux.user.dto.UpdateProfileRequest;
import com.cashflux.user.dto.ChangePasswordRequest;
import com.cashflux.common.exception.ResourceNotFoundException;
import com.cashflux.common.exception.DuplicateResourceException;
import com.cashflux.common.exception.BadRequestException;
import com.cashflux.security.JwtService;
import com.cashflux.user.dto.LoginRequest;
import com.cashflux.user.dto.LoginResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {

        // Check whether email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        // Create new user
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        OffsetDateTime now = OffsetDateTime.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Save user to database
        User savedUser = userRepository.save(user);

        // Return user response
        return new UserResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found")
                );

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public UserResponse updateProfile(
            Long userId,
            UpdateProfileRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found")
                );

        if (userRepository.existsByEmailAndIdNot(
                request.getEmail(),
                userId
        )) {
            throw new DuplicateResourceException("Email is already registered");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setUpdatedAt(OffsetDateTime.now());

        User updatedUser = userRepository.save(user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getFullName(),
                updatedUser.getEmail(),
                updatedUser.getCreatedAt()
        );
    }

    @Transactional
    public void changePassword(
            Long userId,
            ChangePasswordRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                    new NoSuchElementException("User not found")
                );

        //Verify current password
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPasswordHash()
        )) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Prevent using the same password
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPasswordHash()
        )) {
            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        //Update password
        user.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword())
        );

        user.setUpdatedAt(OffsetDateTime.now());

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        String accessToken = jwtService.generateToken(
                user.getId(),
                user.getEmail()
        );

        return new LoginResponse(
                accessToken,
                "Bearer",
                1800L
        );
    }
}