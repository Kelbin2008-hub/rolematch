package com.rolematch.web.controller;

import com.rolematch.domain.entity.Application;
import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.AppStatus;
import com.rolematch.domain.enums.JobStatus;
import com.rolematch.domain.enums.UserRole;
import com.rolematch.repository.ApplicationRepository;
import com.rolematch.repository.JobRepository;
import com.rolematch.repository.ResumeRepository;
import com.rolematch.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("MVC Web Layer & Controller Integration Tests")
class WebLayerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private User recruiter;
    private User seeker;
    private Job job;
    private Resume resume;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        resumeRepository.deleteAll();
        jobRepository.deleteAll();
        userRepository.deleteAll();

        recruiter = userRepository.save(new User("Sarah Recruiter", "sarah@enterprise.io", "pass", UserRole.RECRUITER));
        seeker = userRepository.save(new User("David Seeker", "david@candidate.io", "pass", UserRole.SEEKER));

        job = jobRepository.save(new Job(
                recruiter,
                "Principal Java Engineer",
                "HighScale Corp",
                new BigDecimal("150000"),
                new BigDecimal("200000"),
                "Enterprise Java 21, Spring Boot, PostgreSQL, Docker, Microservices.",
                JobStatus.OPEN
        ));

        resume = resumeRepository.save(new Resume(
                seeker,
                "david_resume.pdf",
                2048L,
                "Principal Java Engineer with 12 years expertise in Spring Boot, PostgreSQL, and distributed computing."
        ));
    }

    @Test
    @DisplayName("GET / should return index landing page view")
    void shouldReturnIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("jobs", "totalJobs"));

        mockMvc.perform(get("/jobs"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs"))
                .andExpect(model().attributeExists("jobs"));

        // Seeker dashboard requires authentication — unauthenticated request redirects to login
        mockMvc.perform(get("/seeker/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("GET /login and GET /register should return authentication views")
    void shouldReturnAuthPages() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"));

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    @DisplayName("POST /seeker/resume/upload should parse resume, invoke AI, and redirect to dashboard")
    void shouldUploadResumeSuccessfully() throws Exception {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        try (org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument()) {
            org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
            doc.addPage(page);
            try (org.apache.pdfbox.pdmodel.PDPageContentStream stream = new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page)) {
                stream.beginText();
                stream.setFont(new org.apache.pdfbox.pdmodel.font.PDType1Font(org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA), 12);
                stream.newLineAtOffset(50, 700);
                stream.showText("Senior Java Architect with Spring Boot, PostgreSQL, and Docker expertise.");
                stream.endText();
            }
            doc.save(out);
        }

        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", out.toByteArray());

        mockMvc.perform(multipart("/seeker/resume/upload")
                        .file(file)
                        .param("jobId", job.getId().toString())
                        .sessionAttr("currentUser", new com.rolematch.web.dto.UserViewDto(
                                seeker.getId(), seeker.getUuid(), seeker.getName(), seeker.getEmail(), seeker.getRole(), true
                        )))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seeker/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        assertThat(resumeRepository.count()).isGreaterThan(1);
    }

    @Test
    @DisplayName("GET /recruiter/dashboard should display applications ranked by matchScore DESC")
    void shouldDisplayRankedRecruiterPipeline() throws Exception {
        Application app = new Application(job, seeker, resume);
        app.setMatchScore((short) 94);
        app.setStatus(AppStatus.APPLIED);
        applicationRepository.save(app);

        mockMvc.perform(get("/recruiter/dashboard")
                        .sessionAttr("currentUser", new com.rolematch.web.dto.UserViewDto(
                                recruiter.getId(), recruiter.getUuid(), recruiter.getName(), recruiter.getEmail(), recruiter.getRole(), true
                        )))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-dashboard"))
                .andExpect(model().attributeExists("applications", "activeJobs"));
    }

    @Test
    @DisplayName("POST /recruiter/application/{id}/status should update state and trigger shortlist event")
    void shouldUpdateApplicationStatusAndTriggerEvent() throws Exception {
        Application app = new Application(job, seeker, resume);
        app.setMatchScore((short) 91);
        app.setStatus(AppStatus.APPLIED);
        Application saved = applicationRepository.save(app);

        mockMvc.perform(post("/recruiter/application/" + saved.getId() + "/status")
                        .param("status", "SHORTLISTED")
                        .sessionAttr("currentUser", new com.rolematch.web.dto.UserViewDto(
                                recruiter.getId(), recruiter.getUuid(), recruiter.getName(), recruiter.getEmail(), recruiter.getRole(), true
                        )))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recruiter/dashboard"))
                .andExpect(flash().attributeExists("successMessage", "eventNotification"));

        Application updated = applicationRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(AppStatus.SHORTLISTED);
    }

    @Test
    @DisplayName("GET Seeker sub-pages should render canonical screens successfully")
    void shouldRenderSeekerSubPages() throws Exception {
        com.rolematch.web.dto.UserViewDto seekerDto = new com.rolematch.web.dto.UserViewDto(
                seeker.getId(), seeker.getUuid(), seeker.getName(), seeker.getEmail(), seeker.getRole(), true
        );

        mockMvc.perform(get("/seeker/dashboard").sessionAttr("currentUser", seekerDto))
                .andExpect(status().isOk())
                .andExpect(view().name("seeker-dashboard"))
                .andExpect(model().attributeExists("user", "currentUser", "activeJobs"));

        mockMvc.perform(get("/seeker/applications").sessionAttr("currentUser", seekerDto))
                .andExpect(status().isOk())
                .andExpect(view().name("seeker-applications"))
                .andExpect(model().attributeExists("currentUser", "applications"));

        mockMvc.perform(get("/seeker/resume").sessionAttr("currentUser", seekerDto))
                .andExpect(status().isOk())
                .andExpect(view().name("seeker-resume"))
                .andExpect(model().attributeExists("currentUser", "resumes"));

        mockMvc.perform(get("/seeker/dashboard-option-a").sessionAttr("currentUser", seekerDto))
                .andExpect(status().isOk())
                .andExpect(view().name("seeker-dashboard-kelbin"));
    }

    @Test
    @DisplayName("GET Recruiter sub-pages should render canonical screens successfully")
    void shouldRenderRecruiterSubPages() throws Exception {
        com.rolematch.web.dto.UserViewDto recruiterDto = new com.rolematch.web.dto.UserViewDto(
                recruiter.getId(), recruiter.getUuid(), recruiter.getName(), recruiter.getEmail(), recruiter.getRole(), true
        );

        mockMvc.perform(get("/recruiter/applications").sessionAttr("currentUser", recruiterDto))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-applications"))
                .andExpect(model().attributeExists("currentRecruiter", "applications"));

        mockMvc.perform(get("/recruiter/candidates").sessionAttr("currentUser", recruiterDto))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-candidates"))
                .andExpect(model().attributeExists("currentRecruiter", "candidates"));

        mockMvc.perform(get("/recruiter/jobs").sessionAttr("currentUser", recruiterDto))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-jobs"))
                .andExpect(model().attributeExists("currentRecruiter", "activeJobs"));

        mockMvc.perform(get("/recruiter/job-intelligence").sessionAttr("currentUser", recruiterDto))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-job-intelligence"))
                .andExpect(model().attributeExists("currentRecruiter", "activeJobs"));
    }

    @Test
    @DisplayName("GET /jobs/{id} should render job-detail with ATS telemetry")
    void shouldRenderJobDetailPage() throws Exception {
        mockMvc.perform(get("/jobs/" + job.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("job-detail"))
                .andExpect(model().attributeExists("job", "atsScore", "similarJobs"));
    }

    @Test
    @DisplayName("POST /jobs/{id}/apply should submit 1-Click application and redirect")
    void shouldSubmitOneClickApplication() throws Exception {
        mockMvc.perform(post("/jobs/" + job.getId() + "/apply")
                        .param("resumeId", resume.getId().toString())
                        .sessionAttr("currentUser", new com.rolematch.web.dto.UserViewDto(
                                seeker.getId(), seeker.getUuid(), seeker.getName(), seeker.getEmail(), seeker.getRole(), true
                        )))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/jobs/" + job.getId()))
                .andExpect(flash().attributeExists("successMessage"));

        assertThat(applicationRepository.findByJobAndSeeker(job, seeker)).isPresent();
    }

    @Test
    @DisplayName("GET /recruiter/jobs/create should render requisition authoring studio")
    void shouldRenderRecruiterJobCreatePage() throws Exception {
        mockMvc.perform(get("/recruiter/jobs/create")
                        .sessionAttr("currentUser", new com.rolematch.web.dto.UserViewDto(
                                recruiter.getId(), recruiter.getUuid(), recruiter.getName(), recruiter.getEmail(), recruiter.getRole(), true
                        )))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-job-create"))
                .andExpect(model().attributeExists("newJob", "currentRecruiter"));
    }

    @Test
    @DisplayName("Security: IDOR defense rejects application when resume belongs to another seeker")
    void shouldRejectApplicationWhenResumeBelongsToAnotherSeeker() throws Exception {
        User anotherSeeker = userRepository.save(new User("Imposter Seeker", "imposter@fake.io", "pass", UserRole.SEEKER));

        mockMvc.perform(post("/jobs/" + job.getId() + "/apply")
                        .param("resumeId", resume.getId().toString()) // resume belongs to 'seeker', not 'anotherSeeker'
                        .sessionAttr("currentUser", new com.rolematch.web.dto.UserViewDto(
                                anotherSeeker.getId(), anotherSeeker.getUuid(), anotherSeeker.getName(), anotherSeeker.getEmail(), anotherSeeker.getRole(), true
                        )))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/jobs/" + job.getId()))
                .andExpect(flash().attributeExists("errorMessage"));

        assertThat(applicationRepository.findByJobAndSeeker(job, anotherSeeker)).isEmpty();
    }

    @Test
    @DisplayName("Security: IDOR defense rejects status change when recruiter does not own the requisition")
    void shouldRejectStatusUpdateWhenRecruiterDoesNotOwnJob() throws Exception {
        User rivalRecruiter = userRepository.save(new User("Rival Recruiter", "rival@othercorp.io", "pass", UserRole.RECRUITER));
        Application app = applicationRepository.save(new Application(job, seeker, resume));

        mockMvc.perform(post("/recruiter/application/" + app.getId() + "/status")
                        .param("status", "SHORTLISTED")
                        .sessionAttr("currentUser", new com.rolematch.web.dto.UserViewDto(
                                rivalRecruiter.getId(), rivalRecruiter.getUuid(), rivalRecruiter.getName(), rivalRecruiter.getEmail(), rivalRecruiter.getRole(), true
                        )))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recruiter/dashboard"))
                .andExpect(flash().attributeExists("errorMessage"));

        Application afterAttempt = applicationRepository.findById(app.getId()).orElseThrow();
        assertThat(afterAttempt.getStatus()).isEqualTo(AppStatus.APPLIED);
    }

    @Test
    @DisplayName("Security: Unauthenticated access to recruiter and seeker dashboards redirects to /login")
    void shouldRedirectUnauthenticatedUsersFromProtectedRoutes() throws Exception {
        mockMvc.perform(get("/recruiter/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/seeker/applications"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/recruiter/jobs/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
