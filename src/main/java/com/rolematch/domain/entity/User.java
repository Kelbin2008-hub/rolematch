package com.rolematch.domain.entity;

import com.rolematch.domain.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import java.util.UUID;

/**
 * Enterprise User domain entity representing platform participants (Seekers, Recruiters, Admins).
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_uuid", columnList = "uuid", unique = true)
})
public class User extends AuditableEntity {

    @NotNull
    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @NotBlank
    @Size(max = 120)
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 32)
    private UserRole role;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    public User() {
        this.uuid = UUID.randomUUID();
    }

    public User(String name, String email, String passwordHash, UserRole role) {
        this();
        setName(name);
        setEmail(email);
        setPasswordHash(passwordHash);
        setRole(role);
        this.active = Boolean.TRUE;
    }

    @Override
    protected void onPrePersist() {
        super.onPrePersist();
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        if (this.active == null) {
            this.active = Boolean.TRUE;
        }
    }

    // Encapsulated Validated Getters and Setters

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        if (this.uuid != null && !this.uuid.equals(uuid)) {
            throw new IllegalStateException("UUID is immutable once established.");
        }
        this.uuid = Objects.requireNonNull(uuid, "UUID cannot be null");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("User name must not be blank.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("User email must not be blank.");
        }
        this.email = email.trim().toLowerCase();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash must not be blank.");
        }
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = Objects.requireNonNull(role, "User role must not be null.");
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active != null ? active : Boolean.TRUE;
    }

    public boolean isSeeker() {
        return UserRole.SEEKER.equals(this.role);
    }

    public boolean isRecruiter() {
        return UserRole.RECRUITER.equals(this.role);
    }

    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return uuid != null && Objects.equals(uuid, user.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}
