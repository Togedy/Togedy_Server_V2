-- JVM 기본 타임존을 Asia/Seoul로 변경하면서, UTC로 저장된 생성·수정 시각을 KST로 옮긴다.
-- last_activated_at, start_time, end_time 등 업무 시각은 이미 KST로 저장되어 있으므로 대상에서 제외한다.
UPDATE users SET created_at = created_at + INTERVAL 9 HOUR, updated_at = updated_at + INTERVAL 9 HOUR;
UPDATE auth_provider SET created_at = created_at + INTERVAL 9 HOUR, updated_at = updated_at + INTERVAL 9 HOUR;
UPDATE planner_daily_image SET created_at = created_at + INTERVAL 9 HOUR, updated_at = updated_at + INTERVAL 9 HOUR;
UPDATE study_task SET created_at = created_at + INTERVAL 9 HOUR, updated_at = updated_at + INTERVAL 9 HOUR;
UPDATE study SET created_at = created_at + INTERVAL 9 HOUR, updated_at = updated_at + INTERVAL 9 HOUR;
UPDATE study_report SET created_at = created_at + INTERVAL 9 HOUR, updated_at = updated_at + INTERVAL 9 HOUR;
UPDATE user_study SET created_at = created_at + INTERVAL 9 HOUR, updated_at = updated_at + INTERVAL 9 HOUR;
UPDATE inquiry SET created_at = created_at + INTERVAL 9 HOUR, updated_at = updated_at + INTERVAL 9 HOUR;
UPDATE notice SET created_at = created_at + INTERVAL 9 HOUR, updated_at = updated_at + INTERVAL 9 HOUR;
UPDATE admin_account SET created_at = created_at + INTERVAL 9 HOUR, updated_at = updated_at + INTERVAL 9 HOUR;
UPDATE calendar_announcement SET created_at = created_at + INTERVAL 9 HOUR, updated_at = updated_at + INTERVAL 9 HOUR;
