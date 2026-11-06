import os
import re

SCREENS_DIR = r"c:\Users\kelbi\.gemini\antigravity-ide\scratch\rolematch\stitch-screens"
TEMPLATES_DIR = r"c:\Users\kelbi\.gemini\antigravity-ide\scratch\rolematch\src\main\resources\templates"

os.makedirs(TEMPLATES_DIR, exist_ok=True)

def add_th_ns(html):
    if 'xmlns:th=' not in html:
        html = re.sub(r'<html\b([^>]*)>', r'<html\1 xmlns:th="http://www.thymeleaf.org">', html, count=1)
    return html

def fix_common_nav_recruiter(html):
    # Recruiter sidebar nav items
    html = re.sub(r'data-path="dashboard"\s+href="#"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'href="#"\s+data-path="dashboard"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'data-path="applications"\s+href="#"', 'th:href="@{/recruiter/applications}" href="/recruiter/applications"', html)
    html = re.sub(r'href="#"\s+data-path="applications"', 'th:href="@{/recruiter/applications}" href="/recruiter/applications"', html)
    html = re.sub(r'data-path="candidates"\s+href="#"', 'th:href="@{/recruiter/candidates}" href="/recruiter/candidates"', html)
    html = re.sub(r'href="#"\s+data-path="candidates"', 'th:href="@{/recruiter/candidates}" href="/recruiter/candidates"', html)
    html = re.sub(r'data-path="manage-jobs"\s+href="#"', 'th:href="@{/recruiter/jobs}" href="/recruiter/jobs"', html)
    html = re.sub(r'href="#"\s+data-path="manage-jobs"', 'th:href="@{/recruiter/jobs}" href="/recruiter/jobs"', html)
    html = re.sub(r'data-path="job-intelligence"\s+href="#"', 'th:href="@{/recruiter/job-intelligence}" href="/recruiter/job-intelligence"', html)
    html = re.sub(r'href="#"\s+data-path="job-intelligence"', 'th:href="@{/recruiter/job-intelligence}" href="/recruiter/job-intelligence"', html)
    html = re.sub(r'data-path="portal-switch"\s+href="#"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'href="#"\s+data-path="portal-switch"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'data-path="recruiter-portal"\s+href="#"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'data-path="seeker-dashboard"\s+href="#"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'data-path="jobs"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)
    html = re.sub(r'data-path="landing"\s+href="#"', 'th:href="@{/}" href="/"', html)
    return html

def fix_common_nav_seeker(html):
    # Seeker sidebar nav items
    html = re.sub(r'data-path="dashboard"\s+href="#"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'href="#"\s+data-path="dashboard"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'data-path="my-applications"\s+href="#"', 'th:href="@{/seeker/applications}" href="/seeker/applications"', html)
    html = re.sub(r'href="#"\s+data-path="my-applications"', 'th:href="@{/seeker/applications}" href="/seeker/applications"', html)
    html = re.sub(r'data-path="resume-hub-and-ast"\s+href="#"', 'th:href="@{/seeker/resume}" href="/seeker/resume"', html)
    html = re.sub(r'href="#"\s+data-path="resume-hub-and-ast"', 'th:href="@{/seeker/resume}" href="/seeker/resume"', html)
    html = re.sub(r'data-path="job-board"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)
    html = re.sub(r'href="#"\s+data-path="job-board"', 'th:href="@{/jobs}" href="/jobs"', html)
    html = re.sub(r'data-path="job-intelligence"\s+href="#"', 'th:href="@{/recruiter/job-intelligence}" href="/recruiter/job-intelligence"', html)
    html = re.sub(r'href="#"\s+data-path="job-intelligence"', 'th:href="@{/recruiter/job-intelligence}" href="/recruiter/job-intelligence"', html)
    html = re.sub(r'data-path="portal-switch"\s+href="#"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'href="#"\s+data-path="portal-switch"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'data-path="seeker-dashboard"\s+href="#"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'data-path="recruiter-portal"\s+href="#"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'data-path="jobs"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)
    html = re.sub(r'data-path="landing"\s+href="#"', 'th:href="@{/}" href="/"', html)
    return html

def flash_alerts():
    return """<!-- Feedback Flash Messages -->
    <div th:if="${successMessage}" class="mb-4 flex items-center gap-2 p-4 rounded-2xl bg-[#1b4332]/80 border border-[#52b788]/50 text-[#8ef2c6] shadow-lg backdrop-blur-xl">
      <span class="material-symbols-outlined text-lg">check_circle</span>
      <span class="text-sm font-medium" th:text="${successMessage}">Success message</span>
    </div>
    <div th:if="${eventNotification}" class="mb-4 flex items-center gap-2 p-4 rounded-2xl bg-secondary-container/60 border border-secondary/50 text-secondary-fixed shadow-lg backdrop-blur-xl">
      <span class="material-symbols-outlined text-lg text-secondary">mark_email_read</span>
      <span class="text-sm font-medium" th:text="${eventNotification}">Notification message</span>
    </div>
    <div th:if="${errorMessage}" class="mb-4 flex items-center gap-2 p-4 rounded-2xl bg-error-container/80 border border-error/50 text-error shadow-lg backdrop-blur-xl">
      <span class="material-symbols-outlined text-lg">error</span>
      <span class="text-sm font-medium" th:text="${errorMessage}">Error message</span>
    </div>"""

# -------------------------------------------------------------
# 1. LANDING PAGE (Screen 1) -> index.html
# -------------------------------------------------------------
def build_index():
    with open(os.path.join(SCREENS_DIR, "01_landing_page_violet.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = re.sub(r'data-path="landing"\s+href="#"', 'th:href="@{/}" href="/"', html)
    html = re.sub(r'data-path="jobs"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)
    html = re.sub(r'data-path="seeker-dashboard"\s+href="#"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'data-path="recruiter-portal"\s+href="#"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'data-path="auth-login"\s+href="#"', 'th:href="@{/login}" href="/login"', html)
    html = re.sub(r'data-path="auth-register"\s+href="#"', 'th:href="@{/register}" href="/register"', html)
    html = re.sub(r'href="#"\s+data-path="auth-login"', 'th:href="@{/login}" href="/login"', html)
    html = re.sub(r'href="#"\s+data-path="auth-register"', 'th:href="@{/register}" href="/register"', html)

    html = html.replace('id="scan-trigger"', 'onclick="window.location.href=\'/seeker/dashboard\'" id="scan-trigger"')

    # Dynamic database jobs grid section
    live_jobs = """<!-- LIVE REQUISITIONS DATABASE SECTION -->
    <section class="w-full px-4 sm:px-8 lg:px-margin py-space-xl bg-surface-container-lowest/50 border-t border-purple-500/15" th:if="${jobs != null and not #lists.isEmpty(jobs)}">
      <div class="max-w-7xl mx-auto flex flex-col gap-space-lg">
        <div class="flex flex-col sm:flex-row items-start sm:items-end justify-between gap-4">
          <div class="space-y-1">
            <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-surface-container-high border border-purple-500/20 text-xs font-semibold text-secondary uppercase tracking-wider">
              <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
              <span>PostgreSQL &amp; Gemini AI Synced</span>
            </div>
            <h2 class="text-2xl sm:text-3xl font-bold text-on-surface">Live Engineering Requisitions</h2>
            <p class="text-sm text-on-surface-variant">Verified high-impact roles calibrated against ATS standards</p>
          </div>
          <a th:href="@{/jobs}" href="/jobs" class="px-5 py-2.5 rounded-full bg-surface-container-high hover:bg-surface-bright text-secondary font-semibold text-xs border border-secondary/30 transition-all flex items-center gap-1.5 shadow-sm">
            <span>Explore All Positions</span>
            <span class="material-symbols-outlined text-sm">arrow_forward</span>
          </a>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-space-md">
          <div th:each="job : ${jobs}" class="p-space-lg rounded-2xl bg-surface-container-low/80 border border-purple-500/20 hover:border-secondary/40 backdrop-blur-xl shadow-xl flex flex-col justify-between gap-4 group transition-all duration-300">
            <div class="space-y-2">
              <div class="flex items-center justify-between text-xs">
                <span class="font-bold text-on-surface" th:text="${job.company()}">Enterprise Co</span>
                <span class="text-emerald-400 font-mono font-semibold" th:text="'$' + ${#numbers.formatDecimal(job.minSalary(), 0, 'COMMA', 0, 'POINT')} + ' - $' + ${#numbers.formatDecimal(job.maxSalary(), 0, 'COMMA', 0, 'POINT')}">$180k - $220k</span>
              </div>
              <h3 class="text-lg font-bold text-on-surface group-hover:text-secondary transition-colors" th:text="${job.title()}">Senior Cloud Architect</h3>
              <p class="text-xs text-on-surface-variant line-clamp-2 leading-relaxed" th:text="${job.description()}">Job description</p>
              <div class="flex flex-wrap gap-1.5 pt-2">
                <span th:each="skill : ${job.skills()}" class="px-2.5 py-0.5 rounded-full bg-secondary-container/30 text-secondary text-[11px] font-medium border border-secondary/20" th:text="${skill}">Java</span>
              </div>
            </div>
            <a th:href="@{/seeker/dashboard(jobId=${job.id()})}" class="w-full py-2.5 rounded-full bg-surface-container-high hover:bg-gradient-to-r hover:from-violet-600 hover:to-fuchsia-600 text-on-surface hover:text-white text-xs font-semibold text-center border border-white/10 hover:border-transparent transition-all shadow-sm flex items-center justify-center gap-1.5">
              <span class="material-symbols-outlined text-sm">bolt</span>
              <span>Match With Resume</span>
            </a>
          </div>
        </div>
      </div>
    </section>
    """
    if '<!-- RECRUITER & CANDIDATE DUAL ENGAGEMENT BANNER -->' in html:
        html = html.replace('<!-- RECRUITER & CANDIDATE DUAL ENGAGEMENT BANNER -->', live_jobs + '\n    <!-- RECRUITER & CANDIDATE DUAL ENGAGEMENT BANNER -->')

    with open(os.path.join(TEMPLATES_DIR, "index.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built index.html")

# -------------------------------------------------------------
# 2. JOB BOARD (Screen 9) -> jobs.html
# -------------------------------------------------------------
def build_jobs():
    with open(os.path.join(SCREENS_DIR, "09_job_board_violet.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = re.sub(r'data-path="landing"\s+href="#"', 'th:href="@{/}" href="/"', html)
    html = re.sub(r'data-path="jobs"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)
    html = re.sub(r'data-path="seeker-dashboard"\s+href="#"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'data-path="recruiter-portal"\s+href="#"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'data-path="auth-login"\s+href="#"', 'th:href="@{/login}" href="/login"', html)
    html = re.sub(r'data-path="auth-register"\s+href="#"', 'th:href="@{/register}" href="/register"', html)

    # Job count dynamic display
    html = re.sub(r'Showing \d+ Verified AI Matches', 'Showing <span th:text="${jobs != null ? jobs.size() : 0}">48</span> Verified Engineering Roles', html)

    # Replace static job cards with dynamic Thymeleaf cards
    start_marker = "<!-- JOB CARD 1:"
    end_marker = "<!-- Pagination Section (Pill Shaped) -->"

    dynamic_card = """<!-- DYNAMIC JOB REQUISITIONS FEED -->
        <div th:if="${jobs == null or #lists.isEmpty(jobs)}" class="p-8 rounded-2xl bg-surface-container-low/80 text-center text-on-surface-variant border border-white/10">
          <span class="material-symbols-outlined text-4xl text-outline mb-2">work_off</span>
          <p class="text-base font-semibold">No active job requisitions found</p>
          <p class="text-xs text-outline mt-1">Check back later or deploy a new role through the recruiter portal.</p>
        </div>

        <article th:each="job : ${jobs}" class="group relative rounded-2xl bg-surface-container-low/85 backdrop-blur-xl p-space-lg lg:p-space-xl border border-white/10 shadow-[0_8px_32px_rgba(0,0,0,0.4)] transition-all duration-300 hover:bg-surface-container/90 hover:border-secondary/40 hover:shadow-[0_12px_36px_rgba(135,57,229,0.35)] mb-4">
          <div class="flex flex-col gap-space-md">
            <!-- Meta & ATS Indicator Row -->
            <div class="flex flex-col md:flex-row md:items-start justify-between gap-space-sm">
              <div class="flex items-start gap-space-md">
                <div class="w-14 h-14 rounded-2xl bg-surface-container-highest/80 flex items-center justify-center shrink-0 border border-white/10 p-2 shadow-inner text-secondary">
                  <span class="material-symbols-outlined text-3xl">cloud_sync</span>
                </div>
                <div class="min-w-0">
                  <div class="flex flex-wrap items-center gap-x-space-sm gap-y-1 text-on-surface-variant text-xs mb-1">
                    <span class="text-on-surface font-bold text-sm" th:text="${job.company()}">Apex Core Cloud</span>
                    <span>•</span>
                    <span class="flex items-center gap-1"><span class="material-symbols-outlined text-sm">location_on</span> Remote / Enterprise</span>
                    <span>•</span>
                    <span class="text-[#38ef7d] font-semibold flex items-center gap-1 font-mono">
                      <span class="material-symbols-outlined text-sm">payments</span>
                      <span th:text="'$' + ${#numbers.formatDecimal(job.minSalary(), 0, 'COMMA', 0, 'POINT')} + ' - $' + ${#numbers.formatDecimal(job.maxSalary(), 0, 'COMMA', 0, 'POINT')} + ' USD'">$185,000 - $220,000</span>
                    </span>
                  </div>
                  <h2 class="text-xl lg:text-2xl font-bold text-on-surface group-hover:text-secondary transition-colors tracking-tight" th:text="${job.title()}">
                    Lead Backend Architect
                  </h2>
                </div>
              </div>
              <div class="flex md:flex-col items-center md:items-end gap-1 shrink-0 self-start">
                <div class="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-[#1b4332]/50 text-[#52b788] border border-[#52b788]/40 shadow-[0_0_16px_rgba(82,183,136,0.3)]">
                  <span class="material-symbols-outlined text-sm">verified</span>
                  <span class="text-xs font-bold">Active Requisition</span>
                </div>
                <span class="text-[11px] font-semibold text-secondary px-2">Gemini Calibrated</span>
              </div>
            </div>
            <!-- Excerpt -->
            <p class="text-sm text-on-surface-variant line-clamp-3 leading-relaxed" th:text="${job.description()}">
              Spearhead high-volume payment routing microservices serving millions of transactions.
            </p>
            <!-- Skill Tag Grid -->
            <div class="flex flex-col gap-space-xs">
              <span class="text-[11px] font-semibold text-outline uppercase tracking-wider">Required Technical Vector Dimensions:</span>
              <div class="flex flex-wrap items-center gap-1.5">
                <span th:each="skill : ${job.skills()}" class="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-secondary-container/30 text-secondary border border-secondary/25 text-xs font-medium">
                  <span class="material-symbols-outlined text-xs">check</span>
                  <span th:text="${skill}">Java 21</span>
                </span>
              </div>
            </div>
            <!-- Action Dock -->
            <div class="flex items-center justify-between pt-space-sm border-t border-white/5">
              <div class="flex items-center gap-space-sm text-on-surface-variant text-xs">
                <span class="flex items-center gap-1 text-secondary font-medium">
                  <span class="material-symbols-outlined text-base">psychology</span>
                  Gemini Ready
                </span>
                <span>•</span>
                <span class="font-mono" th:text="'ROLE-' + ${job.id()}">ROLE-101</span>
              </div>
              <div class="flex items-center gap-space-sm">
                <a th:href="@{/jobs/{id}(id=${job.id()})}" class="px-4 py-2 rounded-full bg-surface-container-high hover:bg-surface-bright text-secondary text-xs font-semibold border border-secondary/30 transition-all flex items-center gap-1.5">
                  <span class="material-symbols-outlined text-sm">visibility</span>
                  <span>View Details &amp; Apply</span>
                </a>
                <a th:href="@{/seeker/dashboard(jobId=${job.id()})}" class="px-5 py-2 rounded-full bg-gradient-to-r from-secondary-container via-[#6332a6] to-[#8739e5] text-on-surface text-xs font-bold border border-white/20 shadow-[0_0_20px_rgba(135,57,229,0.4)] hover:shadow-[0_0_28px_rgba(192,193,255,0.65)] hover:-translate-y-0.5 active:translate-y-0 transition-all flex items-center gap-1.5">
                  <span class="material-symbols-outlined text-base">bolt</span>
                  Quick Apply
                </a>
              </div>
            </div>
          </div>
        </article>
    """
    if start_marker in html and end_marker in html:
        before = html[:html.find(start_marker)]
        after = html[html.find(end_marker):]
        html = before + dynamic_card + "\n        " + after

    with open(os.path.join(TEMPLATES_DIR, "jobs.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built jobs.html")

# -------------------------------------------------------------
# 3. SIGN IN (Screen 5) -> login.html
# -------------------------------------------------------------
def build_login():
    with open(os.path.join(SCREENS_DIR, "05_sign_in_violet.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)

    # Convert static form to Spring MVC form
    html = re.sub(r'<form\b[^>]*>', r'<form action="/login" method="post" th:action="@{/login}">', html)

    # Clean email input: remove name="username", duplicate name="email", hardcoded value
    html = re.sub(r'name="username"', 'name="email"', html)
    html = re.sub(r'value="alex\.morrison@devcorp\.io"', 'th:value="${loginRequest != null ? loginRequest.email() : \'\'}" value=""', html)
    html = re.sub(r'name="email"\s+required\s+name="email"', 'name="email"', html)

    # Clean password input: remove duplicate name="password", hardcoded value
    html = re.sub(r'value="[•\.\*]+"', 'value=""', html)

    # Only show validation feedback micro-card if errorMessage is present
    error_card_marker = '<!-- Validation Feedback Micro-Card -->'
    if error_card_marker in html:
        end_error_card = html.find('</div>\n</div>\n<!-- Form Utilities:', html.find(error_card_marker))
        if end_error_card != -1:
            dynamic_error = """<!-- Validation Feedback Micro-Card -->
    <div th:if="${errorMessage}" class="flex items-center gap-space-xs px-space-md py-1.5 rounded-full bg-error-container text-on-error-container shadow-sm mt-1">
      <span class="material-symbols-outlined text-sm leading-none text-error">error</span>
      <span class="font-label-sm text-label-sm font-medium tracking-normal text-error" th:text="${errorMessage}">
        Invalid credentials. Please verify your email and password.
      </span>
    </div>"""
            html = html[:html.find(error_card_marker)] + dynamic_error + html[end_error_card:]

    # Insert Quick Demo Fill buttons & Flash messages above the form
    demo_controls = """<!-- Auth Feedback Messages -->
    <div th:if="${successMessage}" class="mb-4 flex items-center gap-2 p-3.5 rounded-full bg-[#1b4332]/80 border border-[#52b788]/50 text-[#8ef2c6] text-xs shadow-lg">
      <span class="material-symbols-outlined text-base">check_circle</span>
      <span th:text="${successMessage}">Success message</span>
    </div>
    <!-- Quick Demo Accounts -->
    <div class="mb-4 p-3 rounded-2xl bg-surface-container-low border border-white/10 flex flex-col gap-2">
      <span class="text-[11px] font-mono text-on-surface-variant uppercase tracking-wider">Quick Fill Demo Personas:</span>
      <div class="flex items-center gap-2">
        <button type="button" onclick="document.querySelector('input[name=email]').value='candidate.demo@rolematch.ai';document.querySelector('input[name=password]').value='password123';" class="flex-1 py-1.5 px-3 rounded-full bg-secondary-container/40 text-secondary border border-secondary/30 text-xs font-semibold hover:bg-secondary hover:text-on-secondary transition-all">
          Job Seeker Demo
        </button>
        <button type="button" onclick="document.querySelector('input[name=email]').value='recruiter.demo@rolematch.ai';document.querySelector('input[name=password]').value='password123';" class="flex-1 py-1.5 px-3 rounded-full bg-surface-container-high text-on-surface border border-white/20 text-xs font-semibold hover:bg-surface-bright transition-all">
          Recruiter Demo
        </button>
      </div>
    </div>
    """
    html = re.sub(r'(<form\b[^>]*>)', r'\1\n' + demo_controls, html)

    # Links
    html = re.sub(r'href="#"\s+data-path="register"', 'th:href="@{/register}" href="/register"', html)
    html = re.sub(r'data-path="register"\s+href="#"', 'th:href="@{/register}" href="/register"', html)
    html = re.sub(r'data-path="landing"\s+href="#"', 'th:href="@{/}" href="/"', html)
    html = re.sub(r'data-path="jobs"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)

    with open(os.path.join(TEMPLATES_DIR, "login.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built login.html")

# -------------------------------------------------------------
# 4. REGISTER (Screen 2) -> register.html
# -------------------------------------------------------------
def build_register():
    with open(os.path.join(SCREENS_DIR, "02_register_violet.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)

    # Form attributes
    html = re.sub(r'<form\b[^>]*>', r'<form action="/register" method="post" th:action="@{/register}">', html)

    # Inputs: name, email, password, role
    html = re.sub(r'name="fullName"', 'name="name"', html)
    html = re.sub(r'value="Sarah Jenkins"', 'value=""', html)
    html = re.sub(r'name="workEmail"', 'name="email"', html)
    html = re.sub(r'value="sarah\.invalid@"', 'value=""', html)
    html = re.sub(r'name="email"\s+required\s+name="email"', 'name="email"', html)

    # Remove hardcoded validation error indicators
    html = re.sub(r'bg-error-container/20 text-on-surface', 'bg-surface-container-low text-on-surface', html)
    html = re.sub(r'<!-- Simulated Validation Alert Feedback -->\s*<div[^>]*>.*?</div>', '', html, flags=re.DOTALL)
    html = re.sub(r'<span class="text-label-sm font-label-sm text-error font-medium flex items-center gap-1">.*?</span>', '<span class="text-label-sm font-label-sm text-outline font-medium">REQUIRED</span>', html, flags=re.DOTALL)

    # Insert role radio / select and flash messages
    extra_fields = """<!-- Flash Alerts -->
    <div th:if="${errorMessage}" class="mb-4 flex items-center gap-2 p-3.5 rounded-full bg-error-container/80 border border-error/50 text-error text-xs shadow-lg">
      <span class="material-symbols-outlined text-base">error</span>
      <span th:text="${errorMessage}">Error</span>
    </div>
    <div th:if="${successMessage}" class="mb-4 flex items-center gap-2 p-3.5 rounded-full bg-[#1b4332]/80 border border-[#52b788]/50 text-[#8ef2c6] text-xs shadow-lg">
      <span class="material-symbols-outlined text-base">check_circle</span>
      <span th:text="${successMessage}">Success</span>
    </div>
    <!-- Role Selection -->
    <div class="mb-4 flex flex-col gap-1.5">
      <label class="text-xs font-mono uppercase text-on-surface-variant">Account Persona / Role</label>
      <div class="grid grid-cols-2 gap-2">
        <label class="flex items-center justify-center gap-2 p-2.5 rounded-full bg-surface-container-high border border-white/10 hover:border-secondary/40 cursor-pointer text-xs font-semibold">
          <input type="radio" name="role" value="SEEKER" checked class="accent-secondary"/>
          <span>Candidate (Seeker)</span>
        </label>
        <label class="flex items-center justify-center gap-2 p-2.5 rounded-full bg-surface-container-high border border-white/10 hover:border-secondary/40 cursor-pointer text-xs font-semibold">
          <input type="radio" name="role" value="RECRUITER" class="accent-secondary"/>
          <span>Recruiter / HR</span>
        </label>
      </div>
    </div>
    """
    html = re.sub(r'(<form\b[^>]*>)', r'\1\n' + extra_fields, html)

    # Links
    html = re.sub(r'href="#"\s+data-path="login"', 'th:href="@{/login}" href="/login"', html)
    html = re.sub(r'data-path="login"\s+href="#"', 'th:href="@{/login}" href="/login"', html)
    html = re.sub(r'data-path="landing"\s+href="#"', 'th:href="@{/}" href="/"', html)
    html = re.sub(r'data-path="jobs"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)

    with open(os.path.join(TEMPLATES_DIR, "register.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built register.html")

# -------------------------------------------------------------
# 5. SEEKER DASHBOARD (Screen 4) -> seeker-dashboard.html
# -------------------------------------------------------------
def build_seeker_dashboard():
    with open(os.path.join(SCREENS_DIR, "04_seeker_dashboard_violet.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = fix_common_nav_seeker(html)

    # Dynamic Candidate Name
    html = re.sub(r'<span class="font-semibold text-xs text-on-surface leading-tight">Alex Rivera</span>', '<span class="font-semibold text-xs text-on-surface leading-tight" th:text="${currentUser != null ? currentUser.name() : \'Candidate\'}">Alex Rivera</span>', html)
    html = re.sub(r'<span class="font-medium text-xs text-on-surface truncate">Alex Rivera</span>', '<span class="font-medium text-xs text-on-surface truncate" th:text="${currentUser != null ? currentUser.name() : \'Candidate\'}">Alex Rivera</span>', html)

    # ATS Score Arc & Value
    html = re.sub(r'<span class="font-headline-sm text-3xl font-bold text-on-surface leading-none tracking-tighter">78<span class="text-secondary text-lg font-bold">%</span></span>',
                   '<span class="font-headline-sm text-3xl font-bold text-on-surface leading-none tracking-tighter"><span th:text="${latestResume != null ? latestResume.atsBaseScore() : 78}">78</span><span class="text-secondary text-lg font-bold">%</span></span>', html)

    # Resume Upload Card + Flash Alerts
    upload_card = flash_alerts() + """
    <!-- RESUME UPLOAD MODAL TRIGGER / CARD -->
    <div class="bg-[#21162f]/80 backdrop-blur-2xl p-space-lg rounded-2xl border border-secondary/30 shadow-[0_8px_32px_rgba(0,0,0,0.4)] mb-space-lg flex flex-col md:flex-row items-center justify-between gap-space-md">
      <div class="flex items-center gap-space-md">
        <div class="w-12 h-12 rounded-full bg-secondary-container/40 border border-secondary/40 text-secondary flex items-center justify-center shrink-0">
          <span class="material-symbols-outlined text-2xl">upload_file</span>
        </div>
        <div>
          <h3 class="font-headline-sm text-base font-bold text-on-surface">Upload Candidate Resume (PDF / DOCX)</h3>
          <p class="font-body-sm text-xs text-on-surface-variant">Gemini AI parses competencies, generates interview prep, and calculates ATS vector calibration.</p>
        </div>
      </div>
      <form th:action="@{/seeker/resume/upload}" method="post" enctype="multipart/form-data" class="flex flex-wrap items-center gap-2 w-full md:w-auto">
        <input type="file" name="file" accept=".pdf,.docx,.txt" required class="text-xs text-on-surface-variant file:mr-2 file:py-1.5 file:px-4 file:rounded-full file:border-0 file:text-xs file:font-semibold file:bg-surface-container-high file:text-secondary hover:file:bg-surface-bright cursor-pointer"/>
        <select name="jobId" class="bg-surface-container text-on-surface text-xs rounded-full px-3 py-1.5 border border-surface-bright/40 focus:outline-none cursor-pointer">
          <option value="">Apply to Global Pool</option>
          <option th:each="j : ${activeJobs}" th:value="${j.id()}" th:text="${j.title()} + ' (' + ${j.company()} + ')'">Job Target</option>
        </select>
        <button type="submit" class="px-5 py-2 rounded-full bg-gradient-to-r from-secondary-container via-[#6332a6] to-secondary text-on-surface text-xs font-bold border border-secondary/30 hover:brightness-110 shadow-md flex items-center gap-1.5 transition-all">
          <span class="material-symbols-outlined text-sm">auto_awesome</span>
          <span>Parse &amp; Match</span>
        </button>
      </form>
    </div>
    """
    first_section = '<section class="relative w-full rounded-2xl'
    if first_section in html:
        html = html.replace(first_section, upload_card + '\n' + first_section)

    with open(os.path.join(TEMPLATES_DIR, "seeker-dashboard.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built seeker-dashboard.html")

# -------------------------------------------------------------
# 6. MY APPLICATIONS (Screen 13) -> seeker-applications.html
# -------------------------------------------------------------
def build_seeker_applications():
    with open(os.path.join(SCREENS_DIR, "13_my_applications.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = fix_common_nav_seeker(html)

    # Active count
    html = re.sub(r'<span class="text-4xl font-headline font-bold text-on-surface tracking-tight">\d+</span>',
                  '<span class="text-4xl font-headline font-bold text-on-surface tracking-tight" th:text="${applications != null ? applications.size() : 0}">7</span>', html, count=1)

    # Insert flash alerts at main top
    html = html.replace('<main class="relative pt-16 bg-surface min-h-screen w-full px-6"><div class="flex flex-col w-full pb-16">',
                        '<main class="relative pt-16 bg-surface min-h-screen w-full px-6"><div class="flex flex-col w-full pb-16">\n' + flash_alerts())

    # Dynamic Applications List
    apps_container_marker = '<!-- Applications List Container -->'
    if apps_container_marker in html:
        dynamic_apps_section = """<!-- Applications List Container -->
        <div class="flex flex-col gap-6">
          <div th:if="${applications == null or #lists.isEmpty(applications)}" class="p-12 text-center rounded-2xl bg-surface-container-low border border-white/10 text-on-surface-variant">
            <span class="material-symbols-outlined text-5xl text-outline mb-2">assignment_late</span>
            <h3 class="text-lg font-bold text-on-surface">No Applications Yet</h3>
            <p class="text-xs text-outline mt-1 mb-4">Start by matching your resume with engineering roles on our Job Board.</p>
            <a th:href="@{/jobs}" href="/jobs" class="inline-flex items-center gap-2 px-5 py-2.5 rounded-full bg-secondary text-on-secondary text-xs font-semibold hover:bg-secondary-fixed transition-all shadow-md">
              <span class="material-symbols-outlined text-sm">travel_explore</span>
              <span>Browse Job Requisitions</span>
            </a>
          </div>

          <div th:each="app : ${applications}" class="bg-surface-container-low rounded-DEFAULT p-6 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden flex flex-col gap-6">
            <div class="absolute top-0 right-0 w-72 h-72 bg-secondary/5 rounded-full blur-2xl pointer-events-none"></div>
            <!-- Top Header Row -->
            <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
              <div class="flex items-start gap-4">
                <div class="w-12 h-12 rounded-DEFAULT bg-surface-container-high flex items-center justify-center text-secondary flex-shrink-0 shadow-sm">
                  <span class="material-symbols-outlined text-2xl">cloud_queue</span>
                </div>
                <div class="flex flex-col min-w-0">
                  <div class="flex flex-wrap items-center gap-2.5">
                    <h2 class="text-xl font-headline font-semibold text-on-surface" th:text="${app.jobTitle()}">Lead Backend Architect</h2>
                    <span class="px-3 py-1 rounded-full bg-secondary-container text-on-surface font-label text-xs font-medium tracking-wide flex items-center gap-1.5 shadow-sm">
                      <span class="w-2 h-2 rounded-full bg-secondary animate-ping"></span>
                      <span th:text="${app.status()}">Status</span>
                    </span>
                  </div>
                  <div class="flex flex-wrap items-center gap-y-1 gap-x-3 text-xs font-mono text-outline mt-1.5">
                    <span class="text-on-surface font-medium" th:text="${app.company()}">Apex Core Cloud</span>
                    <span>•</span>
                    <span th:text="'REQ-' + ${app.jobId()}">REQ-2024-8841</span>
                    <span>•</span>
                    <span class="flex items-center gap-1"><span class="material-symbols-outlined text-xs">calendar_today</span> Applied: <span th:text="${#temporals.format(app.appliedAt(), 'MMM dd, yyyy')}">Oct 24, 2026</span></span>
                  </div>
                </div>
              </div>
              <!-- ATS Score Gauge Ring -->
              <div class="flex items-center gap-3 self-start lg:self-center bg-surface-container px-4 py-2 rounded-full">
                <div class="relative w-10 h-10 flex items-center justify-center">
                  <span class="font-headline text-xs font-bold text-secondary" th:text="${app.matchScore()} + '%'">94%</span>
                </div>
                <div class="flex flex-col">
                  <span class="text-[11px] font-label uppercase text-outline">ATS Match</span>
                  <span class="text-xs font-label text-secondary font-semibold">Gemini Resonance</span>
                </div>
              </div>
            </div>
            <!-- Multi-step Pipeline Tracker (5 Stages) -->
            <div class="bg-surface-container p-4 rounded-DEFAULT flex flex-col gap-3">
              <div class="flex items-center justify-between text-xs font-label">
                <span class="text-on-surface font-semibold flex items-center gap-2">
                  <span class="material-symbols-outlined text-secondary text-sm">alt_route</span>
                  Application Lifecycle Progress
                </span>
                <span class="text-secondary font-mono" th:text="'Stage: ' + ${app.status()}">Stage</span>
              </div>
              <div class="grid grid-cols-1 sm:grid-cols-5 gap-2 pt-2">
                <div class="flex flex-col gap-1.5">
                  <div class="h-1.5 w-full bg-secondary rounded-full"></div>
                  <div class="flex items-center gap-1.5">
                    <span class="material-symbols-outlined text-secondary text-sm">check_circle</span>
                    <span class="text-xs font-label text-on-surface font-medium">1. Submitted</span>
                  </div>
                </div>
                <div class="flex flex-col gap-1.5">
                  <div class="h-1.5 w-full bg-secondary rounded-full"></div>
                  <div class="flex items-center gap-1.5">
                    <span class="material-symbols-outlined text-secondary text-sm">check_circle</span>
                    <span class="text-xs font-label text-on-surface font-medium">2. Gemini Parsed</span>
                  </div>
                </div>
                <div class="flex flex-col gap-1.5" th:classappend="${app.status().name() == 'SHORTLISTED' or app.status().name() == 'INTERVIEWING' or app.status().name() == 'ACCEPTED' ? '' : 'opacity-40'}">
                  <div class="h-1.5 w-full bg-secondary rounded-full"></div>
                  <div class="flex items-center gap-1.5">
                    <span class="material-symbols-outlined text-secondary text-sm">check_circle</span>
                    <span class="text-xs font-label text-on-surface font-medium">3. Shortlisted</span>
                  </div>
                </div>
                <div class="flex flex-col gap-1.5" th:classappend="${app.status().name() == 'INTERVIEWING' or app.status().name() == 'ACCEPTED' ? '' : 'opacity-40'}">
                  <div class="h-1.5 w-full bg-secondary rounded-full"></div>
                  <div class="flex items-center gap-1.5">
                    <span class="material-symbols-outlined text-secondary text-sm">play_circle</span>
                    <span class="text-xs font-label text-on-surface font-medium">4. Tech Screen</span>
                  </div>
                </div>
                <div class="flex flex-col gap-1.5" th:classappend="${app.status().name() == 'ACCEPTED' ? '' : 'opacity-40'}">
                  <div class="h-1.5 w-full bg-secondary rounded-full"></div>
                  <div class="flex items-center gap-1.5">
                    <span class="material-symbols-outlined text-secondary text-sm">workspace_premium</span>
                    <span class="text-xs font-label text-on-surface font-medium">5. Final Offer</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        """
        # Find end of the container
        # We replace the hardcoded cards up to pagination or end of container
        idx_start = html.find(apps_container_marker)
        idx_end = html.find('<!-- Pagination / Load More Footer -->', idx_start)
        if idx_end != -1:
            html = html[:idx_start] + dynamic_apps_section + '\n' + html[idx_end:]

    with open(os.path.join(TEMPLATES_DIR, "seeker-applications.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built seeker-applications.html")

# -------------------------------------------------------------
# 7. RESUME HUB & AST (Screen 12) -> seeker-resume.html
# -------------------------------------------------------------
def build_seeker_resume():
    with open(os.path.join(SCREENS_DIR, "12_resume_hub_ast.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = fix_common_nav_seeker(html)

    # Dynamic filename and score
    html = re.sub(r'Sarah_Jenkins_Core_Backend_v4.2.pdf', '<span th:text="${latestResume != null ? latestResume.fileName() : \'Alex_Rivera_Production_Resume.pdf\'}">Alex_Rivera_Production_Resume.pdf</span>', html)
    html = re.sub(r'2.4 MB', '<span th:text="${latestResume != null ? (latestResume.fileSize() / 1024) + \' KB\' : \'1.8 MB\'}">1.8 MB</span>', html)

    # Insert upload form & Flash Alerts
    upload_modal = flash_alerts() + """
    <!-- Upload Resume Action Banner -->
    <div class="p-5 rounded-2xl bg-surface-container-low border border-secondary/30 flex flex-col sm:flex-row items-center justify-between gap-4 shadow-lg mb-6">
      <div class="flex items-center gap-3">
        <span class="material-symbols-outlined text-secondary text-2xl">upload_file</span>
        <div>
          <h3 class="text-sm font-semibold text-on-surface">Update Production Resume AST</h3>
          <p class="text-xs text-on-surface-variant font-mono">Accepts PDF, DOCX, or TXT. Re-indexes lexical vector AST in PostgreSQL.</p>
        </div>
      </div>
      <form th:action="@{/seeker/resume/upload}" method="post" enctype="multipart/form-data" class="flex items-center gap-2">
        <input type="file" name="file" accept=".pdf,.docx,.txt" required class="text-xs text-on-surface-variant file:mr-2 file:py-1 file:px-3 file:rounded-full file:border-0 file:text-xs file:bg-surface-container-high file:text-secondary cursor-pointer"/>
        <button type="submit" class="px-4 py-2 rounded-full bg-secondary text-on-secondary text-xs font-semibold hover:bg-secondary-fixed transition-all flex items-center gap-1">
          <span class="material-symbols-outlined text-sm">auto_awesome</span>
          <span>Upload &amp; Parse</span>
        </button>
      </form>
    </div>
    """
    html = html.replace('<!-- Primary Split Architecture (2 Columns) -->', upload_modal + '\n<!-- Primary Split Architecture (2 Columns) -->')

    with open(os.path.join(TEMPLATES_DIR, "seeker-resume.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built seeker-resume.html")

# -------------------------------------------------------------
# 8. RECRUITER DASHBOARD (Screen 3) -> recruiter-dashboard.html
# -------------------------------------------------------------
def build_recruiter_dashboard():
    with open(os.path.join(SCREENS_DIR, "03_recruiter_dashboard_violet.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = fix_common_nav_recruiter(html)

    # Recruiter Name
    html = re.sub(r'<span class="font-semibold text-xs text-on-surface leading-tight">Elena Vance</span>', '<span class="font-semibold text-xs text-on-surface leading-tight" th:text="${currentRecruiter != null ? currentRecruiter.name() : \'Elena Vance\'}">Elena Vance</span>', html)
    html = re.sub(r'<span class="font-medium text-xs text-on-surface truncate">Elena Vance</span>', '<span class="font-medium text-xs text-on-surface truncate" th:text="${currentRecruiter != null ? currentRecruiter.name() : \'Elena Vance\'}">Elena Vance</span>', html)

    # Metric Cards values
    html = re.sub(r'<h2 class="font-headline-lg text-4xl text-on-surface tracking-tight font-bold leading-none mt-2">14</h2>', '<h2 class="font-headline-lg text-4xl text-on-surface tracking-tight font-bold leading-none mt-2" th:text="${activeJobs != null ? activeJobs.size() : 0}">14</h2>', html)
    html = re.sub(r'<h2 class="font-headline-lg text-4xl text-on-surface tracking-tight font-bold leading-none mt-2">1,428</h2>', '<h2 class="font-headline-lg text-4xl text-on-surface tracking-tight font-bold leading-none mt-2" th:text="${applications != null ? applications.size() : 0}">1,428</h2>', html)

    # Feedback Flash Alerts
    alert_section = flash_alerts()
    html = html.replace('<!-- Pipeline Controls & Global Filter Matrix -->', alert_section + '\n<!-- Pipeline Controls & Global Filter Matrix -->')

    # Update Filter Form select options to show database jobs
    role_filter_needle = '<select class="w-full bg-surface-container-lowest/90 text-on-surface font-body-md text-sm pl-11 pr-10 py-2.5 rounded-full border border-surface-bright/40 focus:outline-none focus:ring-2 focus:ring-secondary/50 appearance-none transition-all cursor-pointer shadow-inner" id="roleFilter">'
    role_filter_replacement = """<select class="w-full bg-surface-container-lowest/90 text-on-surface font-body-md text-sm pl-11 pr-10 py-2.5 rounded-full border border-surface-bright/40 focus:outline-none focus:ring-2 focus:ring-secondary/50 appearance-none transition-all cursor-pointer shadow-inner" id="roleFilter" onchange="window.location.href = this.value ? '/recruiter/dashboard?jobId=' + this.value : '/recruiter/dashboard'">
      <option value="">All Active Requisitions</option>
      <option th:each="j : ${activeJobs}" th:value="${j.id()}" th:text="${j.title()} + ' (' + ${j.company()} + ')'" th:selected="${j.id() == selectedJobId}">Role Title</option>
    """
    if role_filter_needle in html:
        end_select = html.find('</select>', html.find(role_filter_needle))
        html = html[:html.find(role_filter_needle)] + role_filter_replacement + html[end_select:]

    # Dynamic candidates table
    tbody_needle = '<tbody class="divide-y divide-surface-bright/20 font-body-md text-sm text-on-surface">'
    end_tbody = html.find('</tbody>', html.find(tbody_needle))
    dynamic_tbody = """<tbody class="divide-y divide-surface-bright/20 font-body-md text-sm text-on-surface">
      <tr th:if="${applications == null or #lists.isEmpty(applications)}">
        <td colspan="7" class="py-8 text-center text-on-surface-variant">
          <span class="material-symbols-outlined text-4xl text-outline mb-1">group_off</span>
          <p class="text-sm font-semibold">No applicants found for this filter</p>
        </td>
      </tr>
      <tr th:each="app : ${applications}" class="group hover:bg-surface-container-high/40 transition-colors duration-150" th:data-status="${#strings.toLowerCase(app.status())}">
        <td class="py-space-md px-space-lg text-center">
          <input class="candidate-check w-4 h-4 rounded-full bg-surface-container border-surface-bright/60 text-secondary-container focus:ring-0 cursor-pointer" type="checkbox"/>
        </td>
        <td class="py-space-md px-space-md">
          <div class="flex items-center gap-space-md">
            <div class="w-10 h-10 rounded-full bg-surface-container-high border border-secondary/30 flex items-center justify-center text-secondary font-bold text-sm">
              <span th:text="${#strings.substring(app.seekerName(), 0, 1)}">E</span>
            </div>
            <div class="flex flex-col min-w-0">
              <span class="font-headline-lg text-sm text-on-surface font-semibold group-hover:text-secondary transition-colors" th:text="${app.seekerName()}">Candidate Name</span>
              <span class="text-xs text-outline truncate" th:text="${app.jobTitle()}">Applied Requisition</span>
              <span class="text-[11px] text-on-surface-variant font-mono" th:text="${app.seekerEmail()}">email@example.com</span>
            </div>
          </div>
        </td>
        <td class="py-space-md px-space-md">
          <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full border shadow-sm"
               th:classappend="${app.matchScore() >= 80 ? 'bg-[#1b4332]/60 text-[#8ef2c6] border-[#8ef2c6]/40' : (app.matchScore() >= 60 ? 'bg-secondary-container/40 text-secondary border-secondary/30' : 'bg-surface-container-high text-outline border-white/10')}">
            <span class="material-symbols-outlined text-[15px]">verified</span>
            <span class="font-bold text-xs" th:text="${app.matchScore()} + '%'">96%</span>
          </div>
        </td>
        <td class="py-space-md px-space-md max-w-xs">
          <p class="text-xs text-on-surface-variant line-clamp-1" th:text="${app.matchDetails()}">Matched skills summary</p>
        </td>
        <td class="py-space-md px-space-md whitespace-nowrap">
          <span class="font-mono text-xs text-outline" th:text="${#temporals.format(app.appliedAt(), 'MMM dd, yyyy')}">Oct 24, 2026</span>
        </td>
        <td class="py-space-md px-space-md">
          <span class="px-2.5 py-1 rounded-full text-xs font-semibold"
                th:text="${app.status()}"
                th:classappend="${app.status().name() == 'SHORTLISTED' ? 'bg-[#1b4332] text-[#8ef2c6] border border-[#8ef2c6]/30' : (app.status().name() == 'REJECTED' ? 'bg-error-container/40 text-error border border-error/30' : 'bg-secondary-container/40 text-secondary border border-secondary/30')}">
            APPLIED
          </span>
        </td>
        <td class="py-space-md px-space-lg text-right">
          <div class="flex items-center justify-end gap-1.5">
            <form th:action="@{/recruiter/application/{id}/status(id=${app.id()})}" method="post" class="inline-flex items-center">
              <select name="status" class="bg-surface-container-high text-on-surface text-xs rounded-full px-2.5 py-1 border border-surface-bright/40 focus:outline-none cursor-pointer" onchange="this.form.submit()">
                <option th:each="st : ${statuses}" th:value="${st}" th:text="${st}" th:selected="${st == app.status()}">STATUS</option>
              </select>
            </form>
            <button class="p-1.5 rounded-full bg-surface-container-high hover:bg-surface-bright text-secondary hover:text-white transition-all shadow-sm"
                    th:onclick="openScorecardModal([[${app.seekerName()}]], [[${app.matchScore()} + '%']], [[${app.jobTitle()}]], [[${app.matchDetails()}]])"
                    title="View Gemini AI Scorecard">
              <span class="material-symbols-outlined text-[18px]">visibility</span>
            </button>
          </div>
        </td>
      </tr>
    """
    if tbody_needle in html and end_tbody != -1:
        html = html[:html.find(tbody_needle)] + dynamic_tbody + html[end_tbody:]

    # Modal for Job Creation
    modal_needle = '<form class="space-y-space-md" onsubmit="handleRequisitionSubmit(event)">'
    modal_replacement = """<form class="space-y-space-md" th:action="@{/recruiter/job/create}" method="post">
          <div>
            <label class="block font-label-sm text-xs text-outline uppercase tracking-wider mb-1">Target Requisition Title</label>
            <input name="title" class="w-full bg-surface-container-lowest/90 text-on-surface px-4 py-2.5 rounded-full border border-surface-bright/40 focus:outline-none focus:ring-2 focus:ring-secondary/50 text-sm shadow-inner" placeholder="e.g. Staff Distributed Systems Engineer" required type="text"/>
          </div>
          <div>
            <label class="block font-label-sm text-xs text-outline uppercase tracking-wider mb-1">Company / Organization</label>
            <input name="company" class="w-full bg-surface-container-lowest/90 text-on-surface px-4 py-2.5 rounded-full border border-surface-bright/40 focus:outline-none focus:ring-2 focus:ring-secondary/50 text-sm shadow-inner" placeholder="e.g. FinTech Global Labs" required type="text"/>
          </div>
          <div class="grid grid-cols-2 gap-space-md">
            <div>
              <label class="block font-label-sm text-xs text-outline uppercase tracking-wider mb-1">Min Salary ($ USD)</label>
              <input name="minSalary" class="w-full bg-surface-container-lowest/90 text-on-surface px-4 py-2.5 rounded-full border border-surface-bright/40 focus:outline-none focus:ring-2 focus:ring-secondary/50 text-sm shadow-inner" placeholder="140000" required type="number"/>
            </div>
            <div>
              <label class="block font-label-sm text-xs text-outline uppercase tracking-wider mb-1">Max Salary ($ USD)</label>
              <input name="maxSalary" class="w-full bg-surface-container-lowest/90 text-on-surface px-4 py-2.5 rounded-full border border-surface-bright/40 focus:outline-none focus:ring-2 focus:ring-secondary/50 text-sm shadow-inner" placeholder="190000" required type="number"/>
            </div>
          </div>
          <div>
            <label class="block font-label-sm text-xs text-outline uppercase tracking-wider mb-1">Job Description &amp; Scope</label>
            <textarea name="description" rows="3" class="w-full bg-surface-container-lowest/90 text-on-surface p-3 rounded-2xl border border-surface-bright/40 focus:outline-none focus:ring-2 focus:ring-secondary/50 text-sm shadow-inner" placeholder="Architect cloud microservices, lead Kafka pipelines..." required></textarea>
          </div>
          <div>
            <label class="block font-label-sm text-xs text-outline uppercase tracking-wider mb-1">Semantic Skill Requirements (Comma separated)</label>
            <input name="skills" class="w-full bg-surface-container-lowest/90 text-on-surface px-4 py-2.5 rounded-full border border-surface-bright/40 focus:outline-none focus:ring-2 focus:ring-secondary/50 text-sm shadow-inner" placeholder="Java 21, Spring Boot 3, Kafka, AWS, PostgreSQL" type="text"/>
          </div>
    """
    if modal_needle in html:
        end_form_start = html.find('<div class="pt-space-md flex items-center justify-end gap-space-sm', html.find(modal_needle))
        html = html[:html.find(modal_needle)] + modal_replacement + html[end_form_start:]

    with open(os.path.join(TEMPLATES_DIR, "recruiter-dashboard.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built recruiter-dashboard.html")

# -------------------------------------------------------------
# 9. APPLICATIONS PIPELINE (Screen 6) -> recruiter-applications.html
# -------------------------------------------------------------
def build_recruiter_applications():
    with open(os.path.join(SCREENS_DIR, "06_applications_management.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = fix_common_nav_recruiter(html)

    # Dynamic applications table
    tbody_marker = '<tbody class="divide-y divide-surface-container-low font-body text-xs" id="table-body">'
    end_tbody = html.find('</tbody>', html.find(tbody_marker))
    dynamic_tbody = """<tbody class="divide-y divide-surface-container-low font-body text-xs" id="table-body">
      <tr th:if="${applications == null or #lists.isEmpty(applications)}">
        <td colspan="7" class="py-12 text-center text-on-surface-variant">
          <span class="material-symbols-outlined text-4xl text-outline mb-2">assignment_late</span>
          <p class="text-sm font-semibold text-on-surface">No Candidate Applications Active</p>
          <p class="text-xs text-outline mt-1">Applications submitted by candidates will stream into this pipeline.</p>
        </td>
      </tr>
      <tr th:each="app : ${applications}" class="app-row hover:bg-surface-container-high/60 transition-colors">
        <td class="p-4 text-center">
          <input class="row-checkbox accent-secondary rounded cursor-pointer" type="checkbox"/>
        </td>
        <td class="py-4 px-4 font-mono text-on-surface-variant" th:text="'APP-2026-' + ${app.id()}">APP-2026-001</td>
        <td class="py-4 px-4">
          <div class="flex items-center gap-2.5">
            <span class="w-8 h-8 rounded-full bg-surface-container-highest text-secondary font-mono text-xs font-bold flex items-center justify-center" th:text="${#strings.substring(app.seekerName(), 0, 1)}">
              A
            </span>
            <div class="flex flex-col">
              <span class="font-semibold text-on-surface text-sm" th:text="${app.seekerName()}">Candidate</span>
              <span class="text-[10px] font-mono text-on-surface-variant" th:text="${app.seekerEmail()}">email@example.com</span>
            </div>
          </div>
        </td>
        <td class="py-4 px-4">
          <div class="flex items-center gap-1.5">
            <span class="text-on-surface font-medium" th:text="${app.jobTitle()}">Position Title</span>
            <span class="text-[10px] font-mono px-2 py-0.5 rounded-full bg-surface-container-lowest text-secondary" th:text="${app.company()}">Company</span>
          </div>
        </td>
        <td class="py-4 px-4">
          <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-secondary-container/60 text-on-secondary-container font-mono text-xs font-semibold">
            <span class="w-1.5 h-1.5 rounded-full bg-secondary"></span>
            <span th:text="${app.matchScore()} + '%'">96%</span>
          </div>
        </td>
        <td class="py-4 px-4 font-mono text-on-surface-variant" th:text="${#temporals.format(app.appliedAt(), 'yyyy-MM-dd')}">2026-10-24</td>
        <td class="py-4 px-4">
          <form th:action="@{/recruiter/application/{id}/status(id=${app.id()})}" method="post">
            <select name="status" class="stage-selector bg-surface-container-lowest text-on-surface text-xs font-medium py-1.5 px-3 rounded-full focus:outline-none cursor-pointer" onchange="this.form.submit()">
              <option th:each="st : ${statuses}" th:value="${st}" th:text="${st}" th:selected="${st == app.status()}">STATUS</option>
            </select>
          </form>
        </td>
      </tr>
    """
    if tbody_marker in html and end_tbody != -1:
        html = html[:html.find(tbody_marker)] + dynamic_tbody + html[end_tbody:]

    with open(os.path.join(TEMPLATES_DIR, "recruiter-applications.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built recruiter-applications.html")

# -------------------------------------------------------------
# 10. CANDIDATES POOL (Screen 8) -> recruiter-candidates.html
# -------------------------------------------------------------
def build_recruiter_candidates():
    with open(os.path.join(SCREENS_DIR, "08_candidates_pool.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = fix_common_nav_recruiter(html)

    # Insert Flash Alerts
    html = html.replace('<main class="w-full pt-16 bg-surface min-h-screen px-8"><div class="flex flex-col w-full pb-16">',
                        '<main class="w-full pt-16 bg-surface min-h-screen px-8"><div class="flex flex-col w-full pb-16">\n' + flash_alerts())

    with open(os.path.join(TEMPLATES_DIR, "recruiter-candidates.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built recruiter-candidates.html")

# -------------------------------------------------------------
# 11. MANAGE JOBS (Screen 7) -> recruiter-jobs.html
# -------------------------------------------------------------
def build_recruiter_jobs():
    with open(os.path.join(SCREENS_DIR, "07_manage_jobs.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = fix_common_nav_recruiter(html)

    # Flash alerts
    html = html.replace('<main class="w-full pt-16 bg-surface min-h-screen px-8"><div class="flex flex-col w-full pb-16 space-y-8">',
                        '<main class="w-full pt-16 bg-surface min-h-screen px-8"><div class="flex flex-col w-full pb-16 space-y-8">\n' + flash_alerts())

    # Create Requisition Modal injection
    modal = """
    <!-- Create Requisition Modal -->
    <div id="new-req-modal" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-md hidden">
      <div class="relative w-full max-w-xl p-6 rounded-2xl bg-[#21162f] border border-secondary/40 shadow-2xl">
        <div class="flex items-center justify-between pb-4 border-b border-white/10">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-secondary">add_circle</span>
            <h3 class="text-lg font-bold text-on-surface">Deploy New Job Requisition</h3>
          </div>
          <button type="button" onclick="document.getElementById('new-req-modal').classList.add('hidden')" class="p-1 rounded-full text-on-surface-variant hover:text-white">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>
        <form th:action="@{/recruiter/job/create}" method="post" class="mt-4 space-y-4">
          <div>
            <label class="block text-xs font-mono uppercase text-on-surface-variant mb-1">Job Title</label>
            <input type="text" name="title" required placeholder="e.g. Principal Cloud &amp; Distributed Systems Engineer" class="w-full bg-surface-container px-4 py-2.5 rounded-full text-xs text-on-surface focus:outline-none border border-white/10"/>
          </div>
          <div>
            <label class="block text-xs font-mono uppercase text-on-surface-variant mb-1">Company / Organization</label>
            <input type="text" name="company" required placeholder="e.g. Stripe Platform Infrastructure" class="w-full bg-surface-container px-4 py-2.5 rounded-full text-xs text-on-surface focus:outline-none border border-white/10"/>
          </div>
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-mono uppercase text-on-surface-variant mb-1">Min Salary ($)</label>
              <input type="number" name="minSalary" required placeholder="160000" class="w-full bg-surface-container px-4 py-2.5 rounded-full text-xs text-on-surface focus:outline-none border border-white/10"/>
            </div>
            <div>
              <label class="block text-xs font-mono uppercase text-on-surface-variant mb-1">Max Salary ($)</label>
              <input type="number" name="maxSalary" required placeholder="220000" class="w-full bg-surface-container px-4 py-2.5 rounded-full text-xs text-on-surface focus:outline-none border border-white/10"/>
            </div>
          </div>
          <div>
            <label class="block text-xs font-mono uppercase text-on-surface-variant mb-1">Job Description</label>
            <textarea name="description" rows="3" required placeholder="Architect multi-region event-driven microservices..." class="w-full bg-surface-container p-3 rounded-2xl text-xs text-on-surface focus:outline-none border border-white/10"></textarea>
          </div>
          <div>
            <label class="block text-xs font-mono uppercase text-on-surface-variant mb-1">Required Skills (Comma-separated)</label>
            <input type="text" name="skills" placeholder="Java 21, Spring Boot 3, Apache Kafka, PostgreSQL" class="w-full bg-surface-container px-4 py-2.5 rounded-full text-xs text-on-surface focus:outline-none border border-white/10"/>
          </div>
          <div class="flex items-center justify-end gap-3 pt-2">
            <button type="button" onclick="document.getElementById('new-req-modal').classList.add('hidden')" class="px-4 py-2 rounded-full text-xs text-on-surface-variant hover:text-white">Cancel</button>
            <button type="submit" class="px-5 py-2.5 rounded-full bg-secondary text-on-secondary text-xs font-semibold hover:bg-secondary-fixed transition-all shadow-md">Deploy Requisition</button>
          </div>
        </form>
      </div>
    </div>
    """
    html = html.replace('</body>', modal + '\n</body>')
    # Wire the "+ CREATE NEW REQUISITION" button
    html = re.sub(r'(<button\b[^>]*>\s*<span\b[^>]*>add_circle</span>\s*<span>\+\s*CREATE NEW REQUISITION</span>\s*</button>)',
                  r'<div class="flex items-center gap-2"><a th:href="@{/recruiter/jobs/create}" href="/recruiter/jobs/create" class="flex items-center gap-1.5 px-4 py-2.5 rounded-full bg-surface-container-high hover:bg-surface-bright text-secondary text-xs font-semibold border border-secondary/30 transition-all"><span class="material-symbols-outlined text-sm">tune</span><span>Full Studio</span></a><button type="button" onclick="document.getElementById(\'new-req-modal\').classList.remove(\'hidden\')" class="flex items-center gap-2 px-5 py-2.5 rounded-full bg-gradient-to-r from-secondary to-secondary-container text-on-secondary font-headline font-semibold text-xs tracking-wide shadow-lg hover:shadow-xl hover:opacity-95 transition-all"><span class="material-symbols-outlined text-base">add_circle</span><span>+ Quick Requisition</span></button></div>', html)

    # Dynamic Requisitions List from database
    req_list_marker = '<div class="space-y-3" id="requisition-list">'
    if req_list_marker in html:
        dynamic_reqs = """<div class="space-y-3" id="requisition-list">
          <div th:each="job : ${activeJobs}" class="job-item group relative bg-surface-container rounded-2xl p-5 transition-all duration-200 hover:bg-surface-container-high cursor-pointer shadow-md mb-3">
            <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
              <div class="space-y-1.5">
                <div class="flex items-center gap-2.5">
                  <span class="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-mono uppercase bg-surface-container-highest text-secondary">
                    <span class="w-1.5 h-1.5 rounded-full bg-secondary"></span>
                    Active
                  </span>
                  <span class="text-xs font-mono text-outline" th:text="'REQ-' + ${job.id()}">REQ-2024-8841</span>
                  <span class="text-xs font-mono text-on-secondary-container bg-surface-container-lowest px-2 py-0.5 rounded-full">Gemini Calibrated</span>
                </div>
                <h2 class="text-lg font-headline font-semibold text-on-surface group-hover:text-secondary transition-colors" th:text="${job.title()}">
                  Job Requisition Title
                </h2>
                <div class="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-on-surface-variant font-mono">
                  <span class="text-on-surface font-semibold" th:text="${job.company()}">Company</span>
                  <span>•</span>
                  <span class="flex items-center gap-1"><span class="material-symbols-outlined text-xs">payments</span> <span th:text="'$' + ${#numbers.formatDecimal(job.minSalary(), 0, 'COMMA', 0, 'POINT')} + ' - $' + ${#numbers.formatDecimal(job.maxSalary(), 0, 'COMMA', 0, 'POINT')}">$185k - $220k</span></span>
                </div>
                <div class="flex flex-wrap gap-1 pt-1">
                  <span th:each="sk : ${job.skills()}" class="px-2 py-0.5 rounded-full bg-secondary-container/30 text-secondary text-[11px]" th:text="${sk}">Skill</span>
                </div>
              </div>
              <div class="flex items-center gap-3">
                <a th:href="@{/recruiter/applications(jobId=${job.id()})}" class="px-4 py-2 rounded-full bg-surface-container-highest hover:bg-secondary hover:text-on-secondary text-xs font-mono transition-all flex items-center gap-1">
                  <span class="material-symbols-outlined text-sm">person_search</span>
                  <span>View Pipeline</span>
                </a>
              </div>
            </div>
          </div>
        """
        # Replace the hardcoded list
        end_req_list = html.find('</div>\n</div>\n<!-- Right Drawer', html.find(req_list_marker))
        if end_req_list != -1:
            html = html[:html.find(req_list_marker)] + dynamic_reqs + html[end_req_list:]

    with open(os.path.join(TEMPLATES_DIR, "recruiter-jobs.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built recruiter-jobs.html")

# -------------------------------------------------------------
# 12. JOB INTELLIGENCE (Screen 10) -> recruiter-job-intelligence.html
# -------------------------------------------------------------
def build_recruiter_job_intelligence():
    with open(os.path.join(SCREENS_DIR, "10_job_intelligence.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = fix_common_nav_recruiter(html)

    with open(os.path.join(TEMPLATES_DIR, "recruiter-job-intelligence.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built recruiter-job-intelligence.html")

# -------------------------------------------------------------
# 13. KELBIN BIJOY DASHBOARD (Screen 11) -> seeker-dashboard-kelbin.html
# -------------------------------------------------------------
def build_seeker_dashboard_kelbin():
    with open(os.path.join(SCREENS_DIR, "11_dashboard_kelbin_bijoy.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = fix_common_nav_seeker(html)

    with open(os.path.join(TEMPLATES_DIR, "seeker-dashboard-kelbin.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built seeker-dashboard-kelbin.html")

# -------------------------------------------------------------
# 14. JOB DETAIL & 1-CLICK APPLY (Screen 14) -> job-detail.html
# -------------------------------------------------------------
def build_job_detail():
    with open(os.path.join(SCREENS_DIR, "14_job_detail_1_click_apply.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)

    # Nav links
    html = re.sub(r'data-path="landing"\s+href="#"', 'th:href="@{/}" href="/"', html)
    html = re.sub(r'data-path="jobs"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)
    html = re.sub(r'data-path="seeker-dashboard"\s+href="#"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'data-path="recruiter-portal"\s+href="#"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'data-path="auth-login"\s+href="#"', 'th:href="@{/login}" href="/login"', html)
    html = re.sub(r'data-path="auth-register"\s+href="#"', 'th:href="@{/register}" href="/register"', html)
    html = re.sub(r'href="#"\s+data-path="auth-login"', 'th:href="@{/login}" href="/login"', html)
    html = re.sub(r'href="#"\s+data-path="auth-register"', 'th:href="@{/register}" href="/register"', html)

    # Breadcrumbs
    html = html.replace('<a class="hover:text-on-surface transition-colors" href="#">Home</a>',
                        '<a class="hover:text-on-surface transition-colors" th:href="@{/}" href="/">Home</a>')
    html = html.replace('<a class="hover:text-on-surface transition-colors" href="#">Tech Roles</a>',
                        '<a class="hover:text-on-surface transition-colors" th:href="@{/jobs}" href="/jobs">Tech Roles</a>')
    html = html.replace('<span class="text-secondary font-semibold">Lead Backend Architect [REQ-2024-8841]</span>',
                        '<span class="text-secondary font-semibold" th:text="${job.title()} + \' [REQ-\' + ${job.id()} + \']\'">Job Title</span>')
    html = html.replace('#APX-9042', '<span th:text="\'#JOB-\' + ${job.id()}">#JOB-101</span>')

    # Requisition Hero Title & Company
    html = html.replace('Job ID: REQ-2024-8841', 'Job ID: REQ-<span th:text="${job.id()}">101</span>')
    html = re.sub(
        r'<h1 class="text-2xl sm:text-3xl font-bold text-on-surface tracking-tight font-headline">.*?</h1>',
        r'<h1 class="text-2xl sm:text-3xl font-bold text-on-surface tracking-tight font-headline" th:text="${job.title()}">Lead Backend Architect</h1>',
        html,
        count=1
    )
    html = html.replace(
        '<div class="flex items-center gap-2 mt-1 text-sm font-medium text-secondary">\n<span class="material-symbols-outlined text-base">domain</span>\nApex Core Cloud\n</div>',
        '<div class="flex items-center gap-2 mt-1 text-sm font-medium text-secondary">\n<span class="material-symbols-outlined text-base">domain</span>\n<span th:text="${job.company()}">Apex Core Cloud</span>\n</div>'
    )

    # Department & Location
    html = html.replace('San Francisco, CA (Hybrid / 3 Days Node)', '<span th:text="${job.location()}">Remote Node (Global Epoch)</span>')
    html = html.replace('Core Cloud Platform', '<span th:text="${job.department()}">Engineering & Infrastructure</span>')
    html = html.replace(
        '$185,000 - $225,000 USD',
        '<span th:text="\'$\' + ${#numbers.formatDecimal(job.minSalary(), 0, \'COMMA\', 0, \'POINT\')} + \' - $\' + ${#numbers.formatDecimal(job.maxSalary(), 0, \'COMMA\', 0, \'POINT\')} + \' USD / yr\'">$185k - $225k</span>'
    )

    # Description narrative
    desc_needle = "Architect and scale our multi-region event stream pipeline"
    if desc_needle in html:
        desc_block_start = html.rfind('<p class="text-sm text-on-surface-variant leading-relaxed mb-space-md">', 0, html.find(desc_needle))
        if desc_block_start != -1:
            desc_block_end = html.find('</p>', desc_block_start) + 4
            dynamic_desc = '<div class="text-sm text-on-surface-variant leading-relaxed mb-space-md whitespace-pre-line" th:text="${job.description()}">Detailed job description</div>'
            html = html[:desc_block_start] + dynamic_desc + html[desc_block_end:]

    # Dynamic Skills list
    skills_needle = "<!-- Requirement Items -->"
    if skills_needle in html:
        end_skills = html.find('</div>\n</div>\n<!-- Section 3', html.find(skills_needle))
        if end_skills != -1:
            dynamic_skills = """<!-- Dynamic Requirement Items -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-space-sm pt-2">
              <div th:each="skill : ${job.skills()}" class="p-space-sm rounded-DEFAULT bg-surface-container-low flex items-center justify-between border border-purple-500/10">
                <div class="flex items-center gap-2">
                  <span class="material-symbols-outlined text-secondary text-sm">verified</span>
                  <span class="text-xs font-semibold text-on-surface" th:text="${skill}">Java 21</span>
                </div>
                <span class="text-[10px] font-mono px-2 py-0.5 rounded-full bg-secondary-container/20 text-secondary border border-secondary/20">Calibrated Core</span>
              </div>
            </div>
            """
            html = html[:html.find(skills_needle)] + dynamic_skills + html[end_skills:]

    # Right column: Active Resume Widget
    resume_target = 'Sarah_Jenkins_Core_Backend_v4.2.pdf'
    if resume_target in html:
        resume_block_start = html.rfind('<div class="p-space-sm rounded-DEFAULT bg-surface-container-low', 0, html.find(resume_target))
        resume_block_end = html.find('</div>\n</div>\n</div>', resume_block_start) + len('</div>\n</div>\n</div>')
        dynamic_resume = """
        <div class="p-space-sm rounded-DEFAULT bg-surface-container-low flex items-start gap-2.5">
          <span class="material-symbols-outlined text-secondary text-xl flex-shrink-0 mt-0.5">description</span>
          <div class="min-w-0 flex-1">
            <div th:if="${latestResume != null}">
              <div class="text-xs font-semibold text-on-surface truncate" th:text="${latestResume.fileName()}">Active_Resume.pdf</div>
              <div class="text-[11px] text-on-surface-variant flex items-center gap-1.5 mt-0.5">
                <span class="w-1.5 h-1.5 rounded-full bg-secondary"></span>
                <span th:text="'Active Master Profile • ' + (${latestResume.atsBaseScore()} != null ? ${latestResume.atsBaseScore()} : 88) + '% Parsed'">Active Profile</span>
              </div>
            </div>
            <div th:unless="${latestResume != null}">
              <div class="text-xs font-semibold text-on-surface truncate">No Active Resume Detected</div>
              <a th:href="@{/seeker/resume}" class="text-[11px] text-secondary hover:underline flex items-center gap-1 mt-0.5">
                <span class="material-symbols-outlined text-xs">upload_file</span>
                <span>Upload Resume for Gemini Analysis</span>
              </a>
            </div>
          </div>
        </div>
        """
        html = html[:resume_block_start] + dynamic_resume + html[resume_block_end:]

    # ATS Score display
    html = html.replace('<span class="text-base font-black text-secondary">94 / 100</span>',
                        '<span class="text-base font-black text-secondary" th:text="(${atsScore} != null ? ${atsScore} : (${job.atsThreshold()} != null ? ${job.atsThreshold()} : 85)) + \' / 100\'">94 / 100</span>')

    # Apply button / form
    apply_btn_needle = '<button class="w-full py-3.5 px-space-md rounded-full bg-gradient-to-r from-secondary-container to-secondary hover:brightness-110 text-on-surface font-bold text-sm tracking-wide shadow-lg hover:shadow-xl transition-all flex items-center justify-center gap-2 group" id="apply-btn"'
    if apply_btn_needle in html:
        btn_start = html.find(apply_btn_needle)
        btn_end = html.find('</button>', btn_start) + len('</button>')
        dynamic_apply_form = """
        <form th:action="@{/jobs/{id}/apply(id=${job.id()})}" method="post" class="w-full">
          <input type="hidden" name="resumeId" th:if="${latestResume != null}" th:value="${latestResume.id()}" />
          <div th:if="${alreadyApplied}">
            <button disabled type="button" class="w-full py-3.5 px-space-md rounded-full bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 font-bold text-sm tracking-wide shadow-lg flex items-center justify-center gap-2 cursor-default">
              <span class="material-symbols-outlined text-base">task_alt</span>
              <span>Application Submitted (Active)</span>
            </button>
          </div>
          <div th:unless="${alreadyApplied}">
            <button type="submit" class="w-full py-3.5 px-space-md rounded-full bg-gradient-to-r from-secondary-container to-secondary hover:brightness-110 text-on-surface font-bold text-sm tracking-wide shadow-lg hover:shadow-xl transition-all flex items-center justify-center gap-2 group" id="apply-btn">
              <span id="apply-text">Submit 1-Click AI Application</span>
              <span class="material-symbols-outlined text-base group-hover:translate-x-1 transition-transform">arrow_forward</span>
            </button>
          </div>
        </form>
        """
        html = html[:btn_start] + dynamic_apply_form + html[btn_end:]

    # Flash alerts
    html = html.replace('<main class="relative z-10 w-full pt-20 bg-transparent min-h-[calc(100vh-14rem)]"><div class="flex flex-col w-full pb-16">',
                        '<main class="relative z-10 w-full pt-20 bg-transparent min-h-[calc(100vh-14rem)]"><div class="flex flex-col w-full pb-16">\n<div class="max-w-7xl mx-auto w-full px-margin pt-4">' + flash_alerts() + '</div>')

    with open(os.path.join(TEMPLATES_DIR, "job-detail.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built job-detail.html")

# -------------------------------------------------------------
# 15. CREATE JOB REQUISITION (Screen 15) -> recruiter-job-create.html
# -------------------------------------------------------------
def build_recruiter_job_create():
    with open(os.path.join(SCREENS_DIR, "15_create_job_requisition.html"), "r", encoding="utf-8") as f:
        html = f.read()

    html = add_th_ns(html)
    html = fix_common_nav_recruiter(html)

    # Flash alerts at top of workspace
    html = html.replace('<div class="flex items-center justify-between gap-space-md border-b border-white/5 pb-space-lg">',
                        flash_alerts() + '\n<div class="flex items-center justify-between gap-space-md border-b border-white/5 pb-space-lg">')

    # Wrap the form
    publish_btn_old = '<button class="px-6 py-2.5 rounded-full bg-secondary-container text-on-secondary-container hover:bg-secondary-container/90 transition-all duration-200 text-sm font-semibold shadow-md flex items-center gap-space-xs" id="publishBtn">'
    publish_btn_new = '<button type="button" onclick="document.getElementById(\'createJobForm\').submit()" class="px-6 py-2.5 rounded-full bg-secondary-container text-on-secondary-container hover:bg-secondary-container/90 transition-all duration-200 text-sm font-semibold shadow-md flex items-center gap-space-xs" id="publishBtn">'
    html = html.replace(publish_btn_old, publish_btn_new)

    grid_marker = '<div class="grid grid-cols-1 lg:grid-cols-12 gap-space-xl items-start">'
    html = html.replace(grid_marker, '<form id="createJobForm" th:action="@{/recruiter/jobs/create}" method="post">\n<input type="hidden" name="company" th:value="${newJob.company()}" />\n<input type="hidden" name="skills" value="Java 21,Spring Boot 3,Apache Kafka,PostgreSQL,Docker,Kubernetes" id="skillsHiddenInput"/>\n' + grid_marker)

    html = html.replace('</div>\n</div>\n</main>', '</div>\n</div>\n</form>\n</main>')

    # Form Inputs
    html = html.replace(
        '<input class="w-full bg-surface-container px-4 py-3 rounded-full text-sm text-on-surface placeholder:text-outline focus:outline-none focus:bg-surface-container-highest transition-all duration-150" id="jobTitleInput" placeholder="e.g. Principal Vector Engineer" type="text" value="Senior Distributed Systems Engineer"/>',
        '<input class="w-full bg-surface-container px-4 py-3 rounded-full text-sm text-on-surface placeholder:text-outline focus:outline-none focus:bg-surface-container-highest transition-all duration-150" id="jobTitleInput" name="title" required placeholder="e.g. Principal Vector Engineer" type="text" th:value="${newJob.title()}"/>'
    )

    html = html.replace(
        '<input class="w-full bg-surface-container pl-8 pr-4 py-3 rounded-full text-sm text-on-surface focus:outline-none focus:bg-surface-container-highest transition-all" id="salaryMin" placeholder="Min" type="number" value="185000"/>',
        '<input class="w-full bg-surface-container pl-8 pr-4 py-3 rounded-full text-sm text-on-surface focus:outline-none focus:bg-surface-container-highest transition-all" id="salaryMin" name="minSalary" required placeholder="Min" type="number" th:value="${newJob.minSalary()}"/>'
    )

    html = html.replace(
        '<input class="w-full bg-surface-container pl-8 pr-4 py-3 rounded-full text-sm text-on-surface focus:outline-none focus:bg-surface-container-highest transition-all" id="salaryMax" placeholder="Max" type="number" value="225000"/>',
        '<input class="w-full bg-surface-container pl-8 pr-4 py-3 rounded-full text-sm text-on-surface focus:outline-none focus:bg-surface-container-highest transition-all" id="salaryMax" name="maxSalary" required placeholder="Max" type="number" th:value="${newJob.maxSalary()}"/>'
    )

    # Location
    html = html.replace(
        '<input class="w-full bg-surface-container px-4 py-3 rounded-full text-sm text-on-surface focus:outline-none focus:bg-surface-container-highest transition-all" type="text" value="San Francisco Hub or Distributed UTC-8 to UTC-4"/>',
        '<input class="w-full bg-surface-container px-4 py-3 rounded-full text-sm text-on-surface focus:outline-none focus:bg-surface-container-highest transition-all" name="location" type="text" th:value="${newJob.location()}"/>'
    )

    # Department select
    html = html.replace(
        '<select class="w-full appearance-none bg-surface-container px-4 py-3 rounded-full text-sm text-on-surface focus:outline-none focus:bg-surface-container-highest transition-all pr-10">',
        '<select name="department" class="w-full appearance-none bg-surface-container px-4 py-3 rounded-full text-sm text-on-surface focus:outline-none focus:bg-surface-container-highest transition-all pr-10">'
    )

    # Description textarea
    html = re.sub(
        r'<textarea class="w-full bg-surface-container p-4 rounded-2xl text-xs leading-relaxed text-on-surface font-mono resize-none focus:outline-none focus:bg-surface-container-highest transition-all selection:bg-secondary-container" id="jobNarrativeArea" rows="9">.*?</textarea>',
        r'<textarea class="w-full bg-surface-container p-4 rounded-2xl text-xs leading-relaxed text-on-surface font-mono resize-none focus:outline-none focus:bg-surface-container-highest transition-all selection:bg-secondary-container" id="jobNarrativeArea" name="description" rows="9" th:text="${newJob.description()}"></textarea>',
        html,
        flags=re.DOTALL
    )

    # ATS Threshold Slider
    html = html.replace(
        '<input class="w-full h-1.5 bg-surface-container-highest rounded-lg appearance-none cursor-pointer accent-secondary" id="atsSlider" max="95" min="50" type="range" value="80"/>',
        '<input class="w-full h-1.5 bg-surface-container-highest rounded-lg appearance-none cursor-pointer accent-secondary" id="atsSlider" name="atsThreshold" max="95" min="50" type="range" th:value="${newJob.atsThreshold()}"/>'
    )

    with open(os.path.join(TEMPLATES_DIR, "recruiter-job-create.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("Built recruiter-job-create.html")

if __name__ == "__main__":
    build_index()
    build_jobs()
    build_login()
    build_register()
    build_seeker_dashboard()
    build_seeker_applications()
    build_seeker_resume()
    build_recruiter_dashboard()
    build_recruiter_applications()
    build_recruiter_candidates()
    build_recruiter_jobs()
    build_recruiter_job_intelligence()
    build_seeker_dashboard_kelbin()
    build_job_detail()
    build_recruiter_job_create()
    print("All 15 canonical screens built into Thymeleaf templates successfully!")
