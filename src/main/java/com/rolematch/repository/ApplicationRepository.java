package com.rolematch.repository;

import com.rolematch.domain.entity.Application;
import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.AppStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByJobAndSeeker(Job job, User seeker);

    @Query("""
           SELECT a FROM Application a
           JOIN FETCH a.job j
           JOIN FETCH a.resume r
           WHERE a.seeker = :seeker
           ORDER BY a.createdAt DESC
           """)
    List<Application> findBySeekerOrderByCreatedAtDesc(@Param("seeker") User seeker);

    /**
     * Ownership-scoped query: returns applications for a job only when the recruiter owns that job.
     * Prevents IDOR — a recruiter cannot view another recruiter's applicant pool by guessing jobId.
     */
    @Query("""
           SELECT a FROM Application a
           JOIN FETCH a.seeker s
           JOIN FETCH a.resume r
           JOIN FETCH a.job j
           WHERE a.job.id = :jobId
             AND j.recruiter.id = :recruiterId
           ORDER BY a.matchScore DESC NULLS LAST, a.createdAt DESC
           """)
    List<Application> findApplicationsByJobAndRecruiterRanked(@Param("jobId") Long jobId,
                                                               @Param("recruiterId") Long recruiterId);

    /**
     * Pipeline Management Query: Fetches applications for a specific job posting
     * sorted strictly by algorithmic match score descending.
     * Note: use findApplicationsByJobAndRecruiterRanked for recruiter-scoped access.
     */
    @Query("""
           SELECT a FROM Application a
           JOIN FETCH a.seeker s
           JOIN FETCH a.resume r
           JOIN FETCH a.job j
           WHERE a.job.id = :jobId
           ORDER BY a.matchScore DESC NULLS LAST, a.createdAt DESC
           """)
    List<Application> findApplicationsByJobRankedByMatchScore(@Param("jobId") Long jobId);

    /**
     * Pipeline Management Query: Fetches all candidate applications for a recruiter's jobs
     * ranked by match score descending.
     */
    @Query("""
           SELECT a FROM Application a
           JOIN FETCH a.seeker s
           JOIN FETCH a.resume r
           JOIN FETCH a.job j
           WHERE j.recruiter.id = :recruiterId
           ORDER BY a.matchScore DESC NULLS LAST, a.createdAt DESC
           """)
    List<Application> findApplicationsByRecruiterRanked(@Param("recruiterId") Long recruiterId);

    List<Application> findByJobAndStatus(Job job, AppStatus status);
}
