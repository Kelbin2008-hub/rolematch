import os
import re

SCREENS_DIR = r"c:\Users\kelbi\.gemini\antigravity-ide\scratch\rolematch\stitch-screens"
TEMPLATES_DIR = r"c:\Users\kelbi\.gemini\antigravity-ide\scratch\rolematch\src\main\resources\templates"

def build_index():
    src_path = os.path.join(SCREENS_DIR, "3_landing_page.html")
    dest_path = os.path.join(TEMPLATES_DIR, "index.html")
    with open(src_path, "r", encoding="utf-8") as f:
        html = f.read()

    # Add Thymeleaf namespace
    html = html.replace('<html class="dark" lang="en">', '<html class="dark" lang="en" xmlns:th="http://www.thymeleaf.org">')

    # Update nav links
    html = re.sub(r'data-path="landing"\s+href="#"', 'th:href="@{/}" href="/"', html)
    html = re.sub(r'href="#"\s+data-path="landing"', 'th:href="@{/}" href="/"', html)
    html = re.sub(r'data-path="jobs"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)
    html = re.sub(r'data-path="seeker-dashboard"\s+href="#"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'data-path="recruiter-portal"\s+href="#"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'data-path="auth-login"\s+href="#"', 'th:href="@{/login}" href="/login"', html)
    html = re.sub(r'data-path="auth-register"\s+href="#"', 'th:href="@{/register}" href="/register"', html)

    # Scan button click redirects to seeker dashboard
    html = html.replace('id="scan-trigger"', 'onclick="window.location.href=\'/seeker/dashboard\'" id="scan-trigger"')

    # Inject dynamic database jobs grid section before dual engagement banner
    live_jobs_section = """<!-- LIVE REQUISITIONS DATABASE SECTION -->
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
    html = html.replace('<!-- RECRUITER & CANDIDATE DUAL ENGAGEMENT BANNER -->', live_jobs_section + '\n    <!-- RECRUITER & CANDIDATE DUAL ENGAGEMENT BANNER -->')

    with open(dest_path, "w", encoding="utf-8") as f:
        f.write(html)
    print("Generated index.html successfully")

def build_jobs():
    src_path = os.path.join(SCREENS_DIR, "4_job_board.html")
    dest_path = os.path.join(TEMPLATES_DIR, "jobs.html")
    with open(src_path, "r", encoding="utf-8") as f:
        html = f.read()

    # Add Thymeleaf namespace
    html = html.replace('<html class="dark" lang="en">', '<html class="dark" lang="en" xmlns:th="http://www.thymeleaf.org">')

    # Update nav links
    html = re.sub(r'data-path="landing"\s+href="#"', 'th:href="@{/}" href="/"', html)
    html = re.sub(r'data-path="jobs"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)
    html = re.sub(r'data-path="seeker-dashboard"\s+href="#"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'data-path="recruiter-portal"\s+href="#"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)

    # Job count dynamic display
    html = re.sub(r'Showing 48 Verified AI Matches', 'Showing <span th:text="${jobs != null ? jobs.size() : 0}">48</span> Verified Engineering Roles', html)

    # Replace hardcoded job articles with Thymeleaf dynamic loop
    # Find start of JOB CARD 1 and end of JOB CARD 4
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
                <a th:href="@{/seeker/dashboard(jobId=${job.id()})}" class="px-5 py-2 rounded-full bg-gradient-to-r from-secondary-container via-[#6332a6] to-[#8739e5] text-on-surface text-xs font-bold border border-white/20 shadow-[0_0_20px_rgba(135,57,229,0.4)] hover:shadow-[0_0_28px_rgba(192,193,255,0.65)] hover:-translate-y-0.5 active:translate-y-0 transition-all flex items-center gap-1.5">
                  <span class="material-symbols-outlined text-base">bolt</span>
                  Quick Apply with AI Resume
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

    with open(dest_path, "w", encoding="utf-8") as f:
        f.write(html)
    print("Generated jobs.html successfully")

def build_recruiter_dashboard():
    src_path = os.path.join(SCREENS_DIR, "1_recruiter_dashboard.html")
    dest_path = os.path.join(TEMPLATES_DIR, "recruiter-dashboard.html")
    with open(src_path, "r", encoding="utf-8") as f:
        html = f.read()

    # Add Thymeleaf namespace
    html = html.replace('<html class="dark" lang="en">', '<html class="dark" lang="en" xmlns:th="http://www.thymeleaf.org">')

    # Update nav links in aside
    html = re.sub(r'data-path="seeker-dashboard"\s+href="#"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'data-path="recruiter-portal"\s+href="#"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'data-path="jobs"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)
    html = re.sub(r'data-path="landing"\s+href="#"', 'th:href="@{/}" href="/"', html)

    # Recruiter Name in Header / Pill
    html = re.sub(r'<span class="font-semibold text-xs text-on-surface leading-tight">Elena Vance</span>', '<span class="font-semibold text-xs text-on-surface leading-tight" th:text="${currentRecruiter != null ? currentRecruiter.name() : \'Lead Recruiter\'}">Elena Vance</span>', html)
    html = re.sub(r'<span class="font-medium text-xs text-on-surface truncate">Algorithmic User</span>', '<span class="font-medium text-xs text-on-surface truncate" th:text="${currentRecruiter != null ? currentRecruiter.name() : \'Recruiter\'}">Algorithmic User</span>', html)

    # Metric Cards values
    # Total active positions
    html = re.sub(r'<h2 class="font-headline-lg text-4xl text-on-surface tracking-tight font-bold leading-none mt-2">14</h2>', '<h2 class="font-headline-lg text-4xl text-on-surface tracking-tight font-bold leading-none mt-2" th:text="${activeJobs != null ? activeJobs.size() : 0}">14</h2>', html)
    # Total applicants
    html = re.sub(r'<h2 class="font-headline-lg text-4xl text-on-surface tracking-tight font-bold leading-none mt-2">1,428</h2>', '<h2 class="font-headline-lg text-4xl text-on-surface tracking-tight font-bold leading-none mt-2" th:text="${applications != null ? applications.size() : 0}">1,428</h2>', html)

    # Feedback Flash Alerts before Filter Matrix
    alert_section = """<!-- Feedback Messages -->
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
    </div>
    """
    html = html.replace('<!-- Pipeline Controls & Global Filter Matrix -->', alert_section + '\n<!-- Pipeline Controls & Global Filter Matrix -->')

    # Update Filter Form select options to show database jobs
    role_filter_needle = '<select class="w-full bg-surface-container-lowest/90 text-on-surface font-body-md text-sm pl-11 pr-10 py-2.5 rounded-full border border-surface-bright/40 focus:outline-none focus:ring-2 focus:ring-secondary/50 appearance-none transition-all cursor-pointer shadow-inner" id="roleFilter">'
    role_filter_replacement = """<select class="w-full bg-surface-container-lowest/90 text-on-surface font-body-md text-sm pl-11 pr-10 py-2.5 rounded-full border border-surface-bright/40 focus:outline-none focus:ring-2 focus:ring-secondary/50 appearance-none transition-all cursor-pointer shadow-inner" id="roleFilter" onchange="window.location.href = this.value ? '/recruiter/dashboard?jobId=' + this.value : '/recruiter/dashboard'">
      <option value="">All Active Requisitions</option>
      <option th:each="j : ${activeJobs}" th:value="${j.id()}" th:text="${j.title()} + ' (' + ${j.company()} + ')'" th:selected="${j.id() == selectedJobId}">Role Title</option>
    """
    if role_filter_needle in html:
        # replace the select tag and its hardcoded options
        end_select = html.find('</select>', html.find(role_filter_needle))
        html = html[:html.find(role_filter_needle)] + role_filter_replacement + html[end_select:]

    # Update candidate table tbody to iterate dynamically over applications
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
    html = html[:html.find(tbody_needle)] + dynamic_tbody + html[end_tbody:]

    # Update Job Requisition Creation Modal Form to post to /recruiter/job/create
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

    with open(dest_path, "w", encoding="utf-8") as f:
        f.write(html)
    print("Generated recruiter-dashboard.html successfully")

def build_seeker_dashboard():
    src_path = os.path.join(SCREENS_DIR, "2_seeker_dashboard.html")
    dest_path = os.path.join(TEMPLATES_DIR, "seeker-dashboard.html")
    with open(src_path, "r", encoding="utf-8") as f:
        html = f.read()

    # Add Thymeleaf namespace
    html = html.replace('<html class="dark" lang="en">', '<html class="dark" lang="en" xmlns:th="http://www.thymeleaf.org">')

    # Update nav links in aside
    html = re.sub(r'data-path="seeker-dashboard"\s+href="#"', 'th:href="@{/seeker/dashboard}" href="/seeker/dashboard"', html)
    html = re.sub(r'data-path="recruiter-portal"\s+href="#"', 'th:href="@{/recruiter/dashboard}" href="/recruiter/dashboard"', html)
    html = re.sub(r'data-path="jobs"\s+href="#"', 'th:href="@{/jobs}" href="/jobs"', html)
    html = re.sub(r'data-path="landing"\s+href="#"', 'th:href="@{/}" href="/"', html)

    # Candidate Name in Header / Pill
    html = re.sub(r'<span class="font-semibold text-xs text-on-surface leading-tight">Alex Rivera</span>', '<span class="font-semibold text-xs text-on-surface leading-tight" th:text="${currentUser != null ? currentUser.name() : \'Candidate\'}">Alex Rivera</span>', html)
    html = re.sub(r'<span class="font-medium text-xs text-on-surface truncate">Alex Rivera</span>', '<span class="font-medium text-xs text-on-surface truncate" th:text="${currentUser != null ? currentUser.name() : \'Candidate\'}">Alex Rivera</span>', html)

    # ATS Score Arc & Value
    html = re.sub(r'<span class="font-headline-sm text-3xl font-bold text-on-surface leading-none tracking-tighter">78<span class="text-secondary text-lg font-bold">%</span></span>',
                   '<span class="font-headline-sm text-3xl font-bold text-on-surface leading-none tracking-tighter"><span th:text="${latestResume != null ? latestResume.atsBaseScore() : 78}">78</span><span class="text-secondary text-lg font-bold">%</span></span>', html)

    # Flash Messages
    alert_section = """<!-- Feedback Messages -->
    <div th:if="${successMessage}" class="mb-4 flex items-center gap-2 p-4 rounded-2xl bg-[#1b4332]/80 border border-[#52b788]/50 text-[#8ef2c6] shadow-lg backdrop-blur-xl">
      <span class="material-symbols-outlined text-lg">check_circle</span>
      <span class="text-sm font-medium" th:text="${successMessage}">Success message</span>
    </div>
    <div th:if="${errorMessage}" class="mb-4 flex items-center gap-2 p-4 rounded-2xl bg-error-container/80 border border-error/50 text-error shadow-lg backdrop-blur-xl">
      <span class="material-symbols-outlined text-lg">error</span>
      <span class="text-sm font-medium" th:text="${errorMessage}">Error message</span>
    </div>
    """

    # Resume Upload Action Card
    upload_card = """<!-- RESUME UPLOAD MODAL TRIGGER / CARD -->
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

    # Inject right before the first section
    first_section = '<section class="relative w-full rounded-2xl'
    if first_section in html:
        html = html.replace(first_section, alert_section + '\n' + upload_card + '\n' + first_section)

    # Applications list at bottom
    apps_grid_needle = '<div class="grid grid-cols-1 md:grid-cols-3 gap-space-md">'
    end_apps_grid = html.find('</div>\n</section>', html.find(apps_grid_needle))
    dynamic_apps = """<div class="grid grid-cols-1 md:grid-cols-3 gap-space-md">
        <div th:if="${applications == null or #lists.isEmpty(applications)}" class="col-span-3 p-6 text-center text-outline text-xs">
          No applications submitted yet. Upload a resume above or visit the Job Board to apply.
        </div>
        <div th:each="app : ${applications}" class="p-space-md rounded-2xl bg-[#190e27]/70 border border-white/[0.06] backdrop-blur-md space-y-space-sm flex flex-col justify-between hover:border-secondary/30 transition-all">
          <div>
            <div class="flex items-center justify-between mb-space-xs">
              <span class="font-label-sm text-[11px] text-outline uppercase tracking-wider font-semibold" th:text="${app.company()}">Company</span>
              <span class="px-space-md py-0.5 rounded-full bg-[#1b3d2f]/70 text-[#8ef2c6] font-label-sm text-xs font-semibold border border-[#8ef2c6]/30" th:text="${app.matchScore()} + '% Match'">89% Match</span>
            </div>
            <h4 class="font-headline-sm text-base font-bold text-on-surface" th:text="${app.jobTitle()}">Position Title</h4>
            <p class="font-body-sm text-xs text-on-surface-variant mt-1" th:text="${app.matchDetails()}">Match breakdown</p>
          </div>
          <div class="flex items-center justify-between text-outline font-label-sm text-xs pt-space-xs border-t border-white/[0.06]">
            <span th:text="${#temporals.format(app.appliedAt(), 'MMM dd, yyyy')}">Oct 24, 2026</span>
            <span class="text-[#8ef2c6] font-medium flex items-center gap-1 font-semibold" th:text="${app.status()}">
              ACTIVE
            </span>
          </div>
        </div>
      </div>
    """
    if apps_grid_needle in html and end_apps_grid != -1:
        html = html[:html.find(apps_grid_needle)] + dynamic_apps + html[end_apps_grid + 6:]

    with open(dest_path, "w", encoding="utf-8") as f:
        f.write(html)
    print("Generated seeker-dashboard.html successfully")

if __name__ == "__main__":
    build_index()
    build_jobs()
    build_recruiter_dashboard()
    build_seeker_dashboard()
    print("All violet templates successfully integrated!")
