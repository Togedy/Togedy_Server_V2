ALTER TABLE study_time ADD COLUMN last_heartbeat_at DATETIME(6) NULL;

UPDATE study_time SET last_heartbeat_at = start_time
WHERE last_heartbeat_at IS NULL;

ALTER TABLE study_time MODIFY COLUMN last_heartbeat_at DATETIME(6) NOT NULL;

UPDATE study_time st
    JOIN users u ON u.user_id = st.user_id
    SET st.end_time = st.last_heartbeat_at,
        st.is_running = NULL
WHERE u.status = 'STUDYING'
  AND st.end_time IS NULL;

UPDATE users SET status = 'ACTIVE' WHERE status = 'STUDYING';