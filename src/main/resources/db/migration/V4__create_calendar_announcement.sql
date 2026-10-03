CREATE TABLE calendar_announcement (
    calendar_announcement_id  BIGINT       NOT NULL AUTO_INCREMENT,
    user_id                   BIGINT,
    content                   VARCHAR(255) NOT NULL,
    created_at                DATETIME(6),
    updated_at                DATETIME(6),
    PRIMARY KEY (calendar_announcement_id)
);

-- 기존 app_config 공지를 이관한다. 작성자를 알 수 없으므로 user_id는 NULL로 둔다.
INSERT INTO calendar_announcement (user_id, content, created_at, updated_at)
SELECT NULL, announcement, NOW(6), NOW(6)
FROM app_config
WHERE config_key = 'latest_version'
  AND announcement IS NOT NULL
  AND TRIM(announcement) <> '';
