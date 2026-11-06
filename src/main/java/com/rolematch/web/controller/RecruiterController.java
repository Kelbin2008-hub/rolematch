package com.rolematch.web.controller;

import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.AppStatus;
import com.rolematch.domain.enums.UserRole;
import com.rolematch.service.ApplicationService;
import com.rolematch.service.JobService;
import com.rolematch.service.ResumeService;
import com.rolematch.service.UserService;
import com.rolematch.web.dto.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class RecruiterController {

    private static final Logger log = LoggerFactory.getLogger(RecruiterController.class);

    private final ApplicationService applicationService;
    private final JobService jobService;
    private final UserService userService;
    private final ResumeService resumeService;

    public RecruiterController(
            ApplicationService applicationService,
            JobService jobService,
            UserService userService,
            ResumeService resumeService
    ) {
        this.applicationService = applicationService;
        this.jobService = jobService;
        this.userService = userService;
        this.resumeService = resumeService;
    }

    /**
     * Requirement: Recruiter pipeline view. Fetches applications sorted by matchScore DESC.
     */
    @GetMapping("/recruiter/dashboard")
    public String dashboard(
            @RequestParam(value = "jobId", required = false) Long jobId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        if (sessionUser != null && UserRole.SEEKER.equals(sessionUser.role())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Candidate accounts are redirected to the Candidate Dashboard.");
            return "redirect:/seeker/dashboard";
        }

        UserViewDto currentRecruiter = getCurrentRecruiter(session);
        if (currentRecruiter == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to access the Recruiter Portal.");
            return "redirect:/login";
        }

        // Fetch applications sorted by matchScore DESC, scoped to recruiter-owned jobs only
        List<ApplicationViewDto> applications;
        if (jobId != null) {
            applications = applicationService.getRankedApplicationsForJob(jobId, currentRecruiter.id());
        } else {
            applications = applicationService.getRankedApplicationsForRecruiter(currentRecruiter.id());
        }

        List<JobViewDto> activeJobs = jobService.getJobsForRecruiter(currentRecruiter.id());

        model.addAttribute("currentRecruiter", currentRecruiter);
        model.addAttribute("currentUser", currentRecruiter);
        model.addAttribute("user", currentRecruiter);
        model.addAttribute("applications", applications);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("selectedJobId", jobId);
        model.addAttribute("statuses", AppStatus.values());
        model.addAttribute("newJob", new JobCreateRequest("", "", BigDecimal.ZERO, BigDecimal.ZERO, "", List.of()));

        return "recruiter-dashboard";
    }

    @GetMapping("/recruiter/applications")
    public String applications(
            @RequestParam(value = "jobId", required = false) Long jobId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        if (sessionUser != null && UserRole.SEEKER.equals(sessionUser.role())) {
            return "redirect:/seeker/applications";
        }

        UserViewDto currentRecruiter = getCurrentRecruiter(session);
        if (currentRecruiter == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to access the Recruiter Portal.");
            return "redirect:/login";
        }
        List<ApplicationViewDto> applications;
        if (jobId != null) {
            applications = applicationService.getRankedApplicationsForJob(jobId, currentRecruiter.id());
        } else {
            applications = applicationService.getRankedApplicationsForRecruiter(currentRecruiter.id());
        }
        List<JobViewDto> activeJobs = jobService.getJobsForRecruiter(currentRecruiter.id());

        model.addAttribute("currentRecruiter", currentRecruiter);
        model.addAttribute("currentUser", currentRecruiter);
        model.addAttribute("user", currentRecruiter);
        model.addAttribute("applications", applications);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("selectedJobId", jobId);
        model.addAttribute("statuses", AppStatus.values());

        return "recruiter-applications";
    }

    @GetMapping("/recruiter/candidates")
    public String candidates(
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        UserViewDto currentRecruiter = getCurrentRecruiter(session);
        if (currentRecruiter == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to access the Recruiter Portal.");
            return "redirect:/login";
        }
        List<ResumeViewDto> candidates = resumeService.getAllResumes();
        List<JobViewDto> activeJobs = jobService.getActiveJobs();
        List<ApplicationViewDto> applications = applicationService.getRankedApplicationsForRecruiter(currentRecruiter.id());

        model.addAttribute("currentRecruiter", currentRecruiter);
        model.addAttribute("currentUser", currentRecruiter);
        model.addAttribute("user", currentRecruiter);
        model.addAttribute("candidates", candidates);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("applications", applications);

        return "recruiter-candidates";
    }

    @GetMapping("/recruiter/jobs")
    public String jobs(
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        UserViewDto currentRecruiter = getCurrentRecruiter(session);
        if (currentRecruiter == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to access the Recruiter Portal.");
            return "redirect:/login";
        }
        List<JobViewDto> activeJobs = jobService.getJobsForRecruiter(currentRecruiter.id());
        List<ApplicationViewDto> applications = applicationService.getRankedApplicationsForRecruiter(currentRecruiter.id());

        model.addAttribute("currentRecruiter", currentRecruiter);
        model.addAttribute("currentUser", currentRecruiter);
        model.addAttribute("user", currentRecruiter);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("jobs", activeJobs);
        model.addAttribute("applications", applications);
        model.addAttribute("newJob", new JobCreateRequest("", "", BigDecimal.ZERO, BigDecimal.ZERO, "", List.of()));

        return "recruiter-jobs";
    }

    @GetMapping("/recruiter/job-intelligence")
    public String jobIntelligence(
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        UserViewDto currentRecruiter = getCurrentRecruiter(session);
        if (currentRecruiter == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to access the Recruiter Portal.");
            return "redirect:/login";
        }
        List<JobViewDto> activeJobs = jobService.getActiveJobs();
        List<ApplicationViewDto> applications = applicationService.getRankedApplicationsForRecruiter(currentRecruiter.id());
        List<ResumeViewDto> resumes = resumeService.getAllResumes();

        model.addAttribute("currentRecruiter", currentRecruiter);
        model.addAttribute("currentUser", currentRecruiter);
        model.addAttribute("user", currentRecruiter);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("applications", applications);
        model.addAttribute("resumes", resumes);

        return "recruiter-job-intelligence";
    }

    /**
     * Requirement: @PostMapping("/recruiter/application/{id}/status") to update application states
     * and trigger CandidateShortlistedEvent if status is SHORTLISTED.
     * Ownership check: only the recruiter who owns the job can change its applicant statuses.
     */
    @PostMapping("/recruiter/application/{id}/status")
    public String updateApplicationStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") AppStatus status,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        if (sessionUser == null || !UserRole.RECRUITER.equals(sessionUser.role())) {
            if (sessionUser != null && UserRole.SEEKER.equals(sessionUser.role())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Candidate accounts cannot update application statuses.");
                return "redirect:/seeker/dashboard";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to perform this action.");
            return "redirect:/login";
        }

        log.info("Recruiter [{}] updating Application [{}] to status [{}]", sessionUser.id(), id, status);

        try {
            // Ownership check: verify this application belongs to a job owned by the current recruiter
            ApplicationViewDto updated = applicationService.updateApplicationStatusForRecruiter(id, status, sessionUser.id());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Application for " + updated.seekerName() + " updated to " + updated.status() + ".");

            if (AppStatus.SHORTLISTED.equals(status)) {
                redirectAttributes.addFlashAttribute("eventNotification",
                        "Candidate shortlisted! Notification email queued for " + updated.seekerEmail());
            }
        } catch (Exception ex) {
            log.error("Failed to update status for application {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Status update failed: " + ex.getMessage());
        }

        return "redirect:/recruiter/dashboard";
    }

    @GetMapping({"/recruiter/jobs/create", "/recruiter/job/create", "/recruiter/jobs/new"})
    public String showCreateJobForm(
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        if (sessionUser != null && UserRole.SEEKER.equals(sessionUser.role())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Candidate accounts cannot publish job requisitions.");
            return "redirect:/seeker/dashboard";
        }

        UserViewDto currentRecruiter = getCurrentRecruiter(session);
        if (currentRecruiter == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to access the Recruiter Portal.");
            return "redirect:/login";
        }

        model.addAttribute("currentRecruiter", currentRecruiter);
        model.addAttribute("currentUser", currentRecruiter);
        model.addAttribute("user", currentRecruiter);
        if (!model.containsAttribute("newJob")) {
            model.addAttribute("newJob", new JobCreateRequest(
                    "Senior Distributed Systems Engineer",
                    currentRecruiter.name().contains("Stripe") ? "Stripe Platform Infrastructure" : "RoleMatch AI Enterprise",
                    BigDecimal.valueOf(185000),
                    BigDecimal.valueOf(225000),
                    "Architect and scale multi-region event-driven microservices processing high transaction concurrency.",
                    List.of("Java 21", "Spring Boot 3", "PostgreSQL", "Kafka", "Docker", "Kubernetes"),
                    "Infrastructure & Scalability",
                    "Remote Node (Global Epoch)",
                    80
            ));
        }
        return "recruiter-job-create";
    }

    @PostMapping({"/recruiter/job/create", "/recruiter/jobs/create", "/recruiter/jobs/new"})
    public String createJob(
            @Valid @ModelAttribute("newJob") JobCreateRequest request,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        if (sessionUser != null && UserRole.SEEKER.equals(sessionUser.role())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Candidate accounts cannot publish job requisitions.");
            return "redirect:/seeker/dashboard";
        }

        UserViewDto currentRecruiter = getCurrentRecruiter(session);
        if (currentRecruiter == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to publish job requisitions.");
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid job parameters. Please check required fields.");
            return "redirect:/recruiter/jobs/create";
        }

        try {
            JobViewDto createdJob = jobService.createJob(request, currentRecruiter.id());
            redirectAttributes.addFlashAttribute("successMessage", "Job requisition [" + createdJob.title() + "] published successfully to neural board!");
            return "redirect:/recruiter/jobs";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create job posting: " + ex.getMessage());
            return "redirect:/recruiter/jobs/create";
        }
    }

    private UserViewDto getCurrentRecruiter(HttpSession session) {
        UserViewDto user = (UserViewDto) session.getAttribute("currentUser");
        if (user != null && UserRole.RECRUITER.equals(user.role())) {
            return user;
        }
        return null;
    }
}
