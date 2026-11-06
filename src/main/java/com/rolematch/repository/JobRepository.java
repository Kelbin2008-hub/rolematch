package com.rolematch.repository;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(JobStatus status);

    @Query("SELECT DISTINCT j FROM Job j LEFT JOIN FETCH j.skills WHERE j.recruiter = :recruiter ORDER BY j.createdAt DESC")
    List<Job> findByRecruiter(@Param("recruiter") User recruiter);

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.skills WHERE j.id = :id")
    Optional<Job> findByIdWithSkills(@Param("id") Long id);

    @Query("SELECT DISTINCT j FROM Job j LEFT JOIN FETCH j.skills WHERE j.status = :status ORDER BY j.createdAt DESC")
    List<Job> findActiveJobsWithSkills(@Param("status") JobStatus status);
}
