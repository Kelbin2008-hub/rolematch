package com.rolematch.web.controller;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.UserRole;
import com.rolematch.engine.model.MatchResult;
import com.rolematch.engine.strategy.MatchingStrategy;
import com.rolematch.service.ApplicationService;
import com.rolematch.service.JobService;
import com.rolematch.service.ResumeService;
import com.rolematch.service.UserService;
import com.rolematch.web.dto.ApplicationViewDto;
import com.rolematch.web.dto.JobViewDto;
import com.rolematch.web.dto.ResumeViewDto;
import com.rolematch.web.dto.UserLoginRequest;
import com.rolematch.web.dto.UserRegisterRequest;
import com.rolematch.web.dto.UserViewDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class WebController {

    private final JobService jobService;
    private final UserService userService;
    private final ResumeService resumeService;
    private final ApplicationService applicationService;
    private final MatchingStrategy matchingStrategy;

    public WebController(
            JobService jobService,
            UserService userService,
            ResumeService resumeService,
            ApplicationService applicationService,
            MatchingStrategy matchingStrategy
    ) {
        this.jobService = jobService;
        this.userService = userService;
        this.resumeService = resumeService;
        this.applicationService = applicationService;
        this.matchingStrategy = matchingStrategy;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<JobViewDto> jobs = jobService.getActiveJobs();
        model.addAttribute("jobs", jobs);
        model.addAttribute("totalJobs", jobs.size());
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("currentUser") instanceof UserViewDto user) {
            return UserRole.RECRUITER.equals(user.role()) ? "redirect:/recruiter/dashboard" : "redirect:/seeker/dashboard";
        }
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new UserLoginRequest("", ""));
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @Valid @ModelAttribute("loginRequest") UserLoginRequest request,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            UserViewDto user = userService.authenticate(request);
            session.setAttribute("currentUser", user);
            session.setAttribute("role", user.role().name());
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + user.name() + "!");

            if (UserRole.RECRUITER.equals(user.role())) {
                return "redirect:/recruiter/dashboard";
            } else {
                return "redirect:/seeker/dashboard";
            }
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Authentication failed: " + ex.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String register(HttpSession session, Model model) {
        if (session.getAttribute("currentUser") instanceof UserViewDto user) {
            return UserRole.RECRUITER.equals(user.role()) ? "redirect:/recruiter/dashboard" : "redirect:/seeker/dashboard";
        }
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new UserRegisterRequest("", "", "", UserRole.SEEKER));
        }
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @Valid @ModelAttribute("registerRequest") UserRegisterRequest request,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            UserViewDto user = userService.register(request);
            session.setAttribute("currentUser", user);
            session.setAttribute("role", user.role().name());
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully! Welcome, " + user.name());

            if (UserRole.RECRUITER.equals(user.role())) {
                return "redirect:/recruiter/dashboard";
            } else {
                return "redirect:/seeker/dashboard";
            }
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Registration failed: " + ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/jobs")
    public String jobs(Model model) {
        List<JobViewDto> jobs = jobService.getActiveJobs();
        model.addAttribute("jobs", jobs);
        return "jobs";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable("id") Long id, HttpSession session, Model model) {
        JobViewDto job = jobService.getJobById(id);
        model.addAttribute("job", job);

        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        model.addAttribute("currentUser", sessionUser);
        model.addAttribute("user", sessionUser);

        List<JobViewDto> otherJobs = jobService.getActiveJobs().stream()
                .filter(j -> !j.id().equals(id))
                .limit(4)
                .toList();
        model.addAttribute("similarJobs", otherJobs);

        if (sessionUser != null && UserRole.SEEKER.equals(sessionUser.role())) {
            Optional<ResumeViewDto> latestResume = resumeService.getLatestResumeForSeeker(sessionUser.id());
            model.addAttribute("latestResume", latestResume.orElse(null));

            boolean alreadyApplied = applicationService.getApplicationsForSeeker(sessionUser.id()).stream()
                    .anyMatch(app -> app.jobId().equals(id));
            model.addAttribute("alreadyApplied", alreadyApplied);

            if (latestResume.isPresent()) {
                Resume resumeEntity = resumeService.getResumeEntity(latestResume.get().id());
                Job jobEntity = jobService.getJobEntity(id);
                MatchResult matchResult = matchingStrategy.evaluate(resumeEntity, jobEntity);
                model.addAttribute("matchResult", matchResult);
                model.addAttribute("atsScore", (int) Math.round(matchResult.getOverallScore()));
            } else {
                // No resume uploaded — display the job's minimum ATS score requirement
                model.addAttribute("atsScore", job.atsThreshold() != null ? job.atsThreshold() : 75);
            }
        } else {
            // Unauthenticated or recruiter visitor — display the baseline target requirement
            model.addAttribute("latestResume", null);
            model.addAttribute("alreadyApplied", false);
            model.addAttribute("atsScore", job.atsThreshold() != null ? job.atsThreshold() : 75);
        }

        return "job-detail";
    }

    @PostMapping({"/jobs/{id}/apply", "/jobs/apply/{id}"})
    public String applyToJobOneClick(
            @PathVariable("id") Long id,
            @RequestParam(value = "resumeId", required = false) Long resumeId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserViewDto sessionUser = (UserViewDto) session.getAttribute("currentUser");
        if (sessionUser == null || !UserRole.SEEKER.equals(sessionUser.role())) {
            if (sessionUser != null && UserRole.RECRUITER.equals(sessionUser.role())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Recruiters cannot apply to job postings.");
                return "redirect:/recruiter/dashboard";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in as a candidate to apply.");
            return "redirect:/login";
        }

        Long seekerId = sessionUser.id();

        if (resumeId == null) {
            Optional<ResumeViewDto> latest = resumeService.getLatestResumeForSeeker(seekerId);
            if (latest.isPresent()) {
                resumeId = latest.get().id();
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Please upload a resume first before applying.");
                return "redirect:/seeker/resume";
            }
        }

        try {
            ApplicationViewDto application = applicationService.applyToJob(seekerId, id, resumeId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Application submitted to " + application.company() + "! Match Score: " + application.matchScore() + "%.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Application failed: " + ex.getMessage());
        }

        return "redirect:/jobs/" + id;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully.");
        return "redirect:/login";
    }
}
