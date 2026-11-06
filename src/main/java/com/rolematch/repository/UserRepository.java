package com.rolematch.repository;

import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUuid(UUID uuid);

    List<User> findByRole(UserRole role);

    boolean existsByEmail(String email);
}
