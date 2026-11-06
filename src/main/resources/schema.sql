-- ==============================================================================
-- RoleMatch: Production Supabase PostgreSQL Normalized Schema
-- Specification Adherence: UUID, Enum, JSONB, TIMESTAMPTZ, NUMERIC, BOOLEAN
-- ==============================================================================

-- 1. Custom Types / Domain Enums
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role_enum') THEN
        CREATE TYPE user_role_enum AS ENUM ('SEEKER', 'RECRUITER', 'ADMIN');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'job_status_enum') THEN
        CREATE TYPE job_status_enum AS ENUM ('PENDING', 'OPEN', 'REMOVED');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'app_status_enum') THEN
        CREATE TYPE app_status_enum AS ENUM ('APPLIED', 'SHORTLISTED', 'INTERVIEW', 'REJECTED');
    END IF;
END $$;

-- 2. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC')
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_uuid ON users(uuid);

-- 3. Skills Taxonomy Table
CREATE TABLE IF NOT EXISTS skills (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC')
);

CREATE INDEX IF NOT EXISTS idx_skills_name ON skills(name);

-- 4. Jobs Table
CREATE TABLE IF NOT EXISTS jobs (
    id BIGSERIAL PRIMARY KEY,
    recruiter_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    company VARCHAR(150) NOT NULL,
    min_salary NUMERIC(14, 2) NOT NULL CHECK (min_salary >= 0),
    max_salary NUMERIC(14, 2) NOT NULL CHECK (max_salary >= min_salary),
    description TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    department VARCHAR(100) DEFAULT 'Engineering & Infrastructure',
    location VARCHAR(150) DEFAULT 'Remote Node (Global Epoch)',
    ats_threshold INTEGER DEFAULT 75 CHECK (ats_threshold >= 0 AND ats_threshold <= 100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC')
);

-- Idempotent Column Additions for Existing Deployments
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS department VARCHAR(100) DEFAULT 'Engineering & Infrastructure';
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS location VARCHAR(150) DEFAULT 'Remote Node (Global Epoch)';
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS ats_threshold INTEGER DEFAULT 75;

CREATE INDEX IF NOT EXISTS idx_jobs_recruiter_id ON jobs(recruiter_id);
CREATE INDEX IF NOT EXISTS idx_jobs_status ON jobs(status);
CREATE INDEX IF NOT EXISTS idx_jobs_department ON jobs(department);

-- 5. Job - Skill Many-to-Many Join Table
CREATE TABLE IF NOT EXISTS job_skills (
    job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    skill_id BIGINT NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    PRIMARY KEY (job_id, skill_id)
);

CREATE INDEX IF NOT EXISTS idx_job_skills_skill_id ON job_skills(skill_id);

-- 6. Resumes Table (AI Metadata stored in JSONB)
CREATE TABLE IF NOT EXISTS resumes (
    id BIGSERIAL PRIMARY KEY,
    seeker_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL CHECK (file_size >= 0),
    extracted_text TEXT NOT NULL,
    ats_base_score INTEGER CHECK (ats_base_score >= 0 AND ats_base_score <= 100),
    skills_summary JSONB DEFAULT '[]'::jsonb,
    improvement_suggestions JSONB DEFAULT '[]'::jsonb,
    interview_questions JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC')
);

CREATE INDEX IF NOT EXISTS idx_resumes_seeker_id ON resumes(seeker_id);
CREATE INDEX IF NOT EXISTS idx_resumes_skills_jsonb ON resumes USING GIN (skills_summary);

-- 7. Applications Table (Composite Matching Telemetry & JSONB Skill Delta)
CREATE TABLE IF NOT EXISTS applications (
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    seeker_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    resume_id BIGINT NOT NULL REFERENCES resumes(id) ON DELETE CASCADE,
    match_score SMALLINT CHECK (match_score >= 0 AND match_score <= 100),
    status VARCHAR(32) NOT NULL DEFAULT 'APPLIED',
    matched_skills JSONB DEFAULT '[]'::jsonb,
    missing_skills JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
    CONSTRAINT uk_job_seeker_application UNIQUE (job_id, seeker_id)
);

CREATE INDEX IF NOT EXISTS idx_applications_job_match ON applications(job_id, match_score DESC);
CREATE INDEX IF NOT EXISTS idx_applications_seeker_id ON applications(seeker_id);
CREATE INDEX IF NOT EXISTS idx_applications_status ON applications(status);
