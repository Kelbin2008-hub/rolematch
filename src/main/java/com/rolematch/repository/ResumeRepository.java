package com.rolematch.repository;

import com.rolematch.domain.entity.Resume;
import com.rolematch.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    @EntityGraph(attributePaths = {"seeker"})
    List<Resume> findBySeekerOrderByCreatedAtDesc(User seeker);

    @EntityGraph(attributePaths = {"seeker"})
    Optional<Resume> findFirstBySeekerOrderByCreatedAtDesc(User seeker);

    @EntityGraph(attributePaths = {"seeker"})
    List<Resume> findAllByOrderByCreatedAtDesc();
}
