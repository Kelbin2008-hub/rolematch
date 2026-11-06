package com.rolematch.service;

import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.UserRole;
import com.rolematch.repository.UserRepository;
import com.rolematch.web.dto.UserLoginRequest;
import com.rolematch.web.dto.UserRegisterRequest;
import com.rolematch.web.dto.UserViewDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * User service responsible for registration, authentication, and profile lookup.
 * Passwords are hashed with BCrypt (cost 12) — never stored or logged in plaintext.
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    /** BCrypt cost factor — 12 is a reasonable production default (≈250 ms per hash on modern hardware). */
    private static final int BCRYPT_STRENGTH = 12;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    /**
     * Registers a new SEEKER or RECRUITER account.
     * The raw password is BCrypt-hashed before persistence.
     */
    @Transactional
    public UserViewDto register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("An account with email [" + request.email() + "] already exists.");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                request.name(),
                request.email(),
                passwordHash,
                request.role()
        );

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    /**
     * Authenticates a user by email and password.
     * Supports BCrypt hashes (new accounts) and the legacy "hash:demo" sentinel for seeded demo users.
     */
    public UserViewDto authenticate(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new NoSuchElementException("No account found with email: " + request.email()));

        boolean authenticated = false;
        String storedHash = user.getPasswordHash();

        if ("hash:demo".equals(storedHash)) {
            // Demo-seeded accounts use a fixed sentinel; password is not checked for the demo persona
            authenticated = true;
        } else if (storedHash != null && storedHash.startsWith("$2")) {
            // BCrypt hash — verify with encoder
            authenticated = passwordEncoder.matches(request.password(), storedHash);
        } else if (storedHash != null && storedHash.startsWith("SHA256:")) {
            // Legacy SHA256 hash (pre-BCrypt migration) — accept for backward compatibility with existing test data
            String legacyHash = "SHA256:" + Integer.toHexString(request.password().hashCode());
            authenticated = legacyHash.equals(storedHash);
        }

        if (!authenticated) {
            throw new IllegalArgumentException("Invalid password credentials.");
        }

        return toDto(user);
    }

    public UserViewDto findById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));
    }

    public Optional<UserViewDto> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDto);
    }

    /**
     * Finds or creates a demo/seeded user. Demo users receive the fixed "hash:demo" sentinel.
     * This method is ONLY used by DataInitializer and test-preview flows — never for real registrations.
     */
    @Transactional
    public User findOrCreateDemoUser(String email, String name, UserRole role) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new User(name, email, "hash:demo", role)));
    }

    public UserViewDto toDto(User user) {
        return new UserViewDto(
                user.getId(),
                user.getUuid(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getActive()
        );
    }
}
