-- =====================================================================
-- V1: Initial schema (matches all JPA entities as of baseline)
-- =====================================================================

-- users
CREATE TABLE users (
    user_id                    BIGINT       NOT NULL AUTO_INCREMENT,
    nickname                   VARCHAR(10)  NOT NULL,
    email                      VARCHAR(255),
    birth_date                 DATE,
    profile_image_url          VARCHAR(255),
    profile_message            VARCHAR(255),
    planner_visible            BIT(1)       NOT NULL,
    study_streak               INT          NOT NULL,
    last_activated_at          DATETIME(6),
    push_notification_enabled  BIT(1)       NOT NULL,
    marketing_consented        BIT(1)       NOT NULL,
    profile_completed          BIT(1)       NOT NULL,
    status                     VARCHAR(20)  NOT NULL,
    user_role                  VARCHAR(20)  NOT NULL,
    created_at                 DATETIME(6),
    updated_at                 DATETIME(6),
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_nickname (nickname),
    UNIQUE KEY uk_users_email (email)
);

-- auth_provider
CREATE TABLE auth_provider (
    auth_provider_id  BIGINT       NOT NULL AUTO_INCREMENT,
    user_id           BIGINT       NOT NULL,
    provider          VARCHAR(255) NOT NULL,
    provider_user_id  VARCHAR(255) NOT NULL,
    created_at        DATETIME(6),
    updated_at        DATETIME(6),
    PRIMARY KEY (auth_provider_id),
    UNIQUE KEY uk_auth_provider_provider_puid (provider, provider_user_id),
    UNIQUE KEY uk_auth_provider_provider_uid  (provider, user_id),
    CONSTRAINT fk_auth_provider_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- category (depends on users)
CREATE TABLE category (
    category_id  BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(255),
    color        VARCHAR(255),
    status       VARCHAR(255),
    user_id      BIGINT,
    PRIMARY KEY (category_id),
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- user_schedule (depends on users, category)
CREATE TABLE user_schedule (
    user_schedule_id  BIGINT       NOT NULL AUTO_INCREMENT,
    name              VARCHAR(255) NOT NULL,
    memo              VARCHAR(255),
    start_date        DATE         NOT NULL,
    start_time        TIME,
    end_date          DATE,
    end_time          TIME,
    is_d_day          BIT(1)       NOT NULL,
    user_id           BIGINT,
    category_id       BIGINT,
    PRIMARY KEY (user_schedule_id),
    CONSTRAINT fk_user_schedule_user     FOREIGN KEY (user_id)     REFERENCES users (user_id),
    CONSTRAINT fk_user_schedule_category FOREIGN KEY (category_id) REFERENCES category (category_id)
);

-- daily_study_summary
CREATE TABLE daily_study_summary (
    daily_study_summary_id  BIGINT  NOT NULL AUTO_INCREMENT,
    user_id                 BIGINT  NOT NULL,
    study_time              BIGINT  NOT NULL,
    date                    DATE    NOT NULL,
    PRIMARY KEY (daily_study_summary_id),
    UNIQUE KEY uk_daily_study_summary_user_id_date (user_id, date)
);

-- planner_daily_image
CREATE TABLE planner_daily_image (
    planner_daily_image_id  BIGINT       NOT NULL AUTO_INCREMENT,
    user_id                 BIGINT       NOT NULL,
    date                    DATE         NOT NULL,
    image_url               VARCHAR(255),
    created_at              DATETIME(6),
    updated_at              DATETIME(6),
    PRIMARY KEY (planner_daily_image_id),
    UNIQUE KEY uk_planner_daily_image_user_id_date (user_id, date)
);

-- study_subject
CREATE TABLE study_subject (
    study_subject_id  BIGINT       NOT NULL AUTO_INCREMENT,
    user_id           BIGINT       NOT NULL,
    name              VARCHAR(255) NOT NULL,
    color             VARCHAR(255) NOT NULL,
    status            VARCHAR(255) NOT NULL,
    order_index       BIGINT       NOT NULL,
    PRIMARY KEY (study_subject_id)
);

-- study_task (depends on study_subject logically, but no FK in entity)
CREATE TABLE study_task (
    task_id           BIGINT       NOT NULL AUTO_INCREMENT,
    user_id           BIGINT       NOT NULL,
    study_subject_id  BIGINT       NOT NULL,
    name              VARCHAR(255) NOT NULL,
    date              DATE         NOT NULL,
    is_checked        BIT(1)       NOT NULL,
    created_at        DATETIME(6),
    updated_at        DATETIME(6),
    PRIMARY KEY (task_id)
);

-- study_time
CREATE TABLE study_time (
    study_time_id     BIGINT      NOT NULL AUTO_INCREMENT,
    user_id           BIGINT      NOT NULL,
    study_subject_id  BIGINT      NOT NULL,
    start_time        DATETIME(6) NOT NULL,
    end_time          DATETIME(6),
    is_running        BIT(1),
    PRIMARY KEY (study_time_id),
    UNIQUE KEY uk_study_time_user_id_is_running (user_id, is_running)
);

-- study
CREATE TABLE study (
    study_id      BIGINT       NOT NULL AUTO_INCREMENT,
    type          VARCHAR(20)  NOT NULL,
    goal_time     BIGINT,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(255),
    member_count  INT          NOT NULL,
    member_limit  INT          NOT NULL,
    tag           VARCHAR(20)  NOT NULL,
    image_url     VARCHAR(255),
    password      VARCHAR(255),
    tier          VARCHAR(20),
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    PRIMARY KEY (study_id)
);

-- study_report
CREATE TABLE study_report (
    study_report_id  BIGINT       NOT NULL AUTO_INCREMENT,
    user_id          BIGINT       NOT NULL,
    study_id         BIGINT       NOT NULL,
    type             VARCHAR(20)  NOT NULL,
    reason           VARCHAR(255) NOT NULL,
    created_at       DATETIME(6),
    updated_at       DATETIME(6),
    PRIMARY KEY (study_report_id)
);

-- study_statistics
CREATE TABLE study_statistics (
    study_statistics_id  BIGINT  NOT NULL AUTO_INCREMENT,
    study_id             BIGINT  NOT NULL,
    score                BIGINT  NOT NULL,
    streak_days          INT     NOT NULL,
    updated_date         DATE    NOT NULL,
    PRIMARY KEY (study_statistics_id),
    UNIQUE KEY uk_study_statistics_study_id (study_id)
);

-- user_study
CREATE TABLE user_study (
    user_study_id  BIGINT      NOT NULL AUTO_INCREMENT,
    user_id        BIGINT      NOT NULL,
    study_id       BIGINT      NOT NULL,
    role           VARCHAR(20) NOT NULL,
    created_at     DATETIME(6),
    updated_at     DATETIME(6),
    PRIMARY KEY (user_study_id),
    UNIQUE KEY uk_user_study_user_id_study_id (user_id, study_id)
);

-- inquiry
CREATE TABLE inquiry (
    inquiry_id   BIGINT       NOT NULL AUTO_INCREMENT,
    user_id      BIGINT       NOT NULL,
    type         VARCHAR(20)  NOT NULL,
    content      VARCHAR(255) NOT NULL,
    reply_email  VARCHAR(255) NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    created_at   DATETIME(6),
    updated_at   DATETIME(6),
    PRIMARY KEY (inquiry_id)
);

-- notice
CREATE TABLE notice (
    notice_id   BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    content     VARCHAR(255) NOT NULL,
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    PRIMARY KEY (notice_id)
);

-- app_config
CREATE TABLE app_config (
    config_key     VARCHAR(255),
    version_name   VARCHAR(255),
    version_code   BIGINT,
    announcement   VARCHAR(255),
    PRIMARY KEY (config_key)
);

-- university
CREATE TABLE university (
    university_id   BIGINT       NOT NULL AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL,
    admission_type  VARCHAR(255) NOT NULL,
    PRIMARY KEY (university_id),
    UNIQUE KEY uk_university_name_admission_type (name, admission_type)
);

-- university_schedule
CREATE TABLE university_schedule (
    university_schedule_id  BIGINT       NOT NULL AUTO_INCREMENT,
    admission_stage         VARCHAR(255) NOT NULL,
    start_date              DATE         NOT NULL,
    start_time              TIME,
    end_date                DATE,
    end_time                TIME,
    PRIMARY KEY (university_schedule_id)
);

-- university_admission_method (depends on university)
CREATE TABLE university_admission_method (
    university_admission_method_id  BIGINT       NOT NULL AUTO_INCREMENT,
    name                            VARCHAR(255) NOT NULL,
    academic_year                   YEAR         NOT NULL,
    university_id                   BIGINT       NOT NULL,
    PRIMARY KEY (university_admission_method_id),
    UNIQUE KEY uk_university_admission_method_uid_name (university_id, name),
    CONSTRAINT fk_university_admission_method_university
        FOREIGN KEY (university_id) REFERENCES university (university_id)
);

-- university_admission_schedule (depends on university_admission_method, university_schedule)
CREATE TABLE university_admission_schedule (
    university_admission_schedule_id  BIGINT  NOT NULL AUTO_INCREMENT,
    university_admission_method_id    BIGINT  NOT NULL,
    university_schedule_id            BIGINT  NOT NULL,
    PRIMARY KEY (university_admission_schedule_id),
    UNIQUE KEY uk_university_admission_schedule (university_admission_method_id, university_schedule_id),
    CONSTRAINT fk_univ_admission_schedule_method
        FOREIGN KEY (university_admission_method_id) REFERENCES university_admission_method (university_admission_method_id),
    CONSTRAINT fk_univ_admission_schedule_schedule
        FOREIGN KEY (university_schedule_id) REFERENCES university_schedule (university_schedule_id)
);

-- user_university_method (depends on users, university_admission_method)
CREATE TABLE user_university_method (
    user_university_method_id       BIGINT  NOT NULL AUTO_INCREMENT,
    user_id                         BIGINT  NOT NULL,
    university_admission_method_id  BIGINT  NOT NULL,
    PRIMARY KEY (user_university_method_id),
    UNIQUE KEY uk_user_university_method (user_id, university_admission_method_id),
    CONSTRAINT fk_user_university_method_user
        FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_user_university_method_admission_method
        FOREIGN KEY (university_admission_method_id) REFERENCES university_admission_method (university_admission_method_id)
);
