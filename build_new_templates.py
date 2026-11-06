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
    html = re.sub(r'data-path="auth-login"\s+href="#"', 'th:href="@{/login}" href="/login"', html)
    html = re.sub(r'data-path="auth-register"\s+href="#"', 'th:href="@{/register}" href="/register"', html)
    html = re.sub(r'data-path="logout"\s+href="#"', 'th:href="@{/logout}" href="/logout"', html)
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
# Screen 14: JOB DETAIL & 1-CLICK APPLY -> job-detail.html
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

    # Department & Location & Compensation
    html = html.replace('San Francisco, CA (Hybrid / 3 Days Node)', '<span th:text="${job.location()}">Remote Node (Global Epoch)</span>')
    html = html.replace('Core Cloud Platform', '<span th:text="${job.department()}">Engineering & Infrastructure</span>')
    html = html.replace(
        '$185,000 - $225,000 USD',
        '<span th:text="\'$\' + ${#numbers.formatDecimal(job.minSalary(), 0, \'COMMA\', 0, \'POINT\')} + \' - $\' + ${#numbers.formatDecimal(job.maxSalary(), 0, \'COMMA\', 0, \'POINT\')} + \' USD / yr\'">$185k - $225k</span>'
    )

    # Replace resume text safely
    html = html.replace(
        'Sarah_Jenkins_Core_Backend_v4.2.pdf',
        '<span th:text="${latestResume != null ? latestResume.fileName() : \'Active Resume Profile\'}">Active_Resume.pdf</span>'
    )
    html = html.replace(
        'Active Master Profile • 92% Parsed',
        'Active Master Profile • <span th:text="(${latestResume != null and latestResume.atsBaseScore() != null} ? ${latestResume.atsBaseScore()} : 88) + \'% Parsed\'">92% Parsed</span>'
    )

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
# Screen 15: CREATE JOB REQUISITION -> recruiter-job-create.html
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
    build_job_detail()
    build_recruiter_job_create()
