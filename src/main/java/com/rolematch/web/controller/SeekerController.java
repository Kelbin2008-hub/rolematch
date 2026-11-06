package com.rolematch.web.controller;

import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.UserRole;
import com.rolematch.service.ApplicationService;
import com.rolematch.service.JobService;
import com.rolematch.service.ResumeService;
import com.rolematch.service.UserService;
import com.rolematch.web.dto.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class SeekerController {

    private static final Logger log = LoggerFactory.getLogger(SeekerController.class);

    private final ResumeService resumeService;
    private final ApplicationService applicationService;
    private final JobService jobService;
    private final UserService userService;

    public SeekerController(
            ResumeService resumeService,
            ApplicationService applicationService,
            JobService jobService,
            UserService userService
    ) {
        this.resumeService = resumeService;
        this.applicationService = applicationService;
        this.jobService = jobService;
        this.userService = userService;
    }

    @GetMapping("/seeker/dashboard")
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        if (sessionUser != null && UserRole.RECRUITER.equals(sessionUser.role())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Recruiter accounts are redirected to the Recruiter Portal.");
            return "redirect:/recruiter/dashboard";
        }

        UserViewDto currentUser = getCurrentSeeker(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to access your dashboard.");
            return "redirect:/login";
        }

        List<ResumeViewDto> resumes = resumeService.getResumesForSeeker(currentUser.id());
        List<ApplicationViewDto> applications = applicationService.getApplicationsForSeeker(currentUser.id());
        List<JobViewDto> activeJobs = jobService.getActiveJobs();
        Optional<ResumeViewDto> latestResume = resumeService.getLatestResumeForSeeker(currentUser.id());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", currentUser);
        model.addAttribute("resumes", resumes);
        model.addAttribute("applications", applications);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("latestResume", latestResume.orElse(null));

        return "seeker-dashboard";
    }

    @GetMapping("/seeker/applications")
    public String applications(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        if (sessionUser != null && UserRole.RECRUITER.equals(sessionUser.role())) {
            return "redirect:/recruiter/applications";
        }

        UserViewDto currentUser = getCurrentSeeker(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to view your applications.");
            return "redirect:/login";
        }
        List<ApplicationViewDto> applications = applicationService.getApplicationsForSeeker(currentUser.id());
        List<ResumeViewDto> resumes = resumeService.getResumesForSeeker(currentUser.id());
        List<JobViewDto> activeJobs = jobService.getActiveJobs();
        Optional<ResumeViewDto> latestResume = resumeService.getLatestResumeForSeeker(currentUser.id());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", currentUser);
        model.addAttribute("applications", applications);
        model.addAttribute("resumes", resumes);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("latestResume", latestResume.orElse(null));

        return "seeker-applications";
    }

    @GetMapping("/seeker/resume")
    public String resumeHub(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        if (sessionUser != null && UserRole.RECRUITER.equals(sessionUser.role())) {
            return "redirect:/recruiter/dashboard";
        }

        UserViewDto currentUser = getCurrentSeeker(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to access your Resume Hub.");
            return "redirect:/login";
        }
        List<ResumeViewDto> resumes = resumeService.getResumesForSeeker(currentUser.id());
        Optional<ResumeViewDto> latestResume = resumeService.getLatestResumeForSeeker(currentUser.id());
        List<ApplicationViewDto> applications = applicationService.getApplicationsForSeeker(currentUser.id());
        List<JobViewDto> activeJobs = jobService.getActiveJobs();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", currentUser);
        model.addAttribute("resumes", resumes);
        model.addAttribute("latestResume", latestResume.orElse(null));
        model.addAttribute("applications", applications);
        model.addAttribute("activeJobs", activeJobs);

        return "seeker-resume";
    }

    @GetMapping({"/seeker/dashboard-option-a", "/seeker/dashboard-kelbin"})
    public String dashboardKelbin(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        UserViewDto currentUser = getCurrentSeeker(session);
        List<ResumeViewDto> resumes = resumeService.getResumesForSeeker(currentUser.id());
        List<ApplicationViewDto> applications = applicationService.getApplicationsForSeeker(currentUser.id());
        List<JobViewDto> activeJobs = jobService.getActiveJobs();
        Optional<ResumeViewDto> latestResume = resumeService.getLatestResumeForSeeker(currentUser.id());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", currentUser);
        model.addAttribute("resumes", resumes);
        model.addAttribute("applications", applications);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("latestResume", latestResume.orElse(null));

        return "seeker-dashboard-kelbin";
    }

    /**
     * Requirement: Handle @PostMapping("/seeker/resume/upload").
     * Orchestrates ResumeParserFactory, passes text to GeminiAIService, persists Resume,
     * and redirects to dashboard.
     */
    @PostMapping("/seeker/resume/upload")
    public String uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobId", required = false) Long jobId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        if (sessionUser == null || !UserRole.SEEKER.equals(sessionUser.role())) {
            if (sessionUser != null && UserRole.RECRUITER.equals(sessionUser.role())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Recruiters cannot upload seeker resumes.");
                return "redirect:/recruiter/dashboard";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to upload your resume.");
            return "redirect:/login";
        }

        UserViewDto currentUser = sessionUser;
        log.info("Processing resume upload for Seeker ID [{}], Job Context [{}]", currentUser.id(), jobId);

        try {
            ResumeViewDto savedResume = resumeService.processAndSaveResume(file, currentUser.id(), jobId);

            // If a specific job was selected during upload, automatically create the application
            if (jobId != null) {
                applicationService.applyToJob(currentUser.id(), jobId, savedResume.id());
                redirectAttributes.addFlashAttribute("successMessage",
                        "Resume analyzed by Gemini AI! Base ATS Score: " + savedResume.atsBaseScore() + "%. Application submitted!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Resume parsed and analyzed successfully! Gemini ATS Score: " + savedResume.atsBaseScore() + "%.");
            }
        } catch (Exception ex) {
            log.error("Resume upload failed for Seeker [{}]: {}", currentUser.id(), ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Upload processing failed: " + ex.getMessage());
        }

        return "redirect:/seeker/dashboard";
    }

    @PostMapping("/seeker/apply")
    public String applyToJob(
            @RequestParam("jobId") Long jobId,
            @RequestParam("resumeId") Long resumeId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        if (sessionUser == null || !UserRole.SEEKER.equals(sessionUser.role())) {
            if (sessionUser != null && UserRole.RECRUITER.equals(sessionUser.role())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Recruiters cannot apply to candidate openings.");
                return "redirect:/recruiter/dashboard";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to apply for jobs.");
            return "redirect:/login";
        }

        UserViewDto currentUser = sessionUser;
        try {
            ApplicationViewDto application = applicationService.applyToJob(currentUser.id(), jobId, resumeId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Application submitted to " + application.company() + "! Match Score: " + application.matchScore() + "%.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Application failed: " + ex.getMessage());
        }
        return "redirect:/seeker/dashboard";
    }

    /**
     * Returns the authenticated seeker from session.
     * Returns null if the session is unauthenticated — callers must check and redirect.
     */
    private UserViewDto getCurrentSeeker(HttpSession session) {
        UserViewDto user = (UserViewDto) session.getAttribute("currentUser");
        if (user != null && UserRole.SEEKER.equals(user.role())) {
            return user;
        }
        return null;
    }
}
