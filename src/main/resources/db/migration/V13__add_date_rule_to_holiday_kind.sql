-- 연휴 종류에 날짜 규칙을 추가한다. 규칙이 없는(MANUAL) 종류는 어드민이 날짜를 직접 지정한다.
ALTER TABLE holiday_kind
    ADD COLUMN date_rule_type VARCHAR(20) NOT NULL DEFAULT 'MANUAL' AFTER badge_enabled,
    ADD COLUMN rule_month INT NULL AFTER date_rule_type,
    ADD COLUMN rule_day INT NULL AFTER rule_month,
    ADD COLUMN days_before INT NOT NULL DEFAULT 0 AFTER rule_day,
    ADD COLUMN days_after INT NOT NULL DEFAULT 0 AFTER days_before;

ALTER TABLE holiday_kind
    ALTER COLUMN date_rule_type DROP DEFAULT,
    ALTER COLUMN days_before DROP DEFAULT,
    ALTER COLUMN days_after DROP DEFAULT;

-- 기획 지정 공휴일을 등록한다. 같은 이름의 종류가 이미 있으면 날짜 규칙을 덮어쓰고 지급을 켠다.
INSERT INTO holiday_kind (name, badge_image_url, badge_enabled, date_rule_type, rule_month, rule_day,
                          days_before, days_after, created_at, updated_at)
VALUES ('신정', NULL, b'1', 'SOLAR', 1, 1, 0, 0, NOW(6), NOW(6)),
       ('설날', NULL, b'1', 'LUNAR', 1, 1, 1, 1, NOW(6), NOW(6)),
       ('삼일절', NULL, b'1', 'SOLAR', 3, 1, 0, 0, NOW(6), NOW(6)),
       ('노동절', NULL, b'1', 'SOLAR', 5, 1, 0, 0, NOW(6), NOW(6)),
       ('어린이날', NULL, b'1', 'SOLAR', 5, 5, 0, 0, NOW(6), NOW(6)),
       ('부처님오신날', NULL, b'1', 'LUNAR', 4, 8, 0, 0, NOW(6), NOW(6)),
       ('현충일', NULL, b'1', 'SOLAR', 6, 6, 0, 0, NOW(6), NOW(6)),
       ('제헌절', NULL, b'1', 'SOLAR', 7, 17, 0, 0, NOW(6), NOW(6)),
       ('광복절', NULL, b'1', 'SOLAR', 8, 15, 0, 0, NOW(6), NOW(6)),
       ('추석', NULL, b'1', 'LUNAR', 8, 15, 1, 1, NOW(6), NOW(6)),
       ('개천절', NULL, b'1', 'SOLAR', 10, 3, 0, 0, NOW(6), NOW(6)),
       ('한글날', NULL, b'1', 'SOLAR', 10, 9, 0, 0, NOW(6), NOW(6)),
       ('크리스마스', NULL, b'1', 'SOLAR', 12, 25, 0, 0, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE badge_enabled  = VALUES(badge_enabled),
                        date_rule_type = VALUES(date_rule_type),
                        rule_month     = VALUES(rule_month),
                        rule_day       = VALUES(rule_day),
                        days_before    = VALUES(days_before),
                        days_after     = VALUES(days_after),
                        updated_at     = VALUES(updated_at);

-- 기존 연휴는 여러 날짜 기간으로 등록되어 있어 삭제하고, 날짜 규칙으로 다시 생성한다.
-- 기존 연휴로 지급된 연휴 파이터 뱃지는 테스트 용도이므로 함께 삭제한다.
DELETE FROM badge WHERE type = 'HOLIDAY';
DELETE FROM holiday;

-- 같은 날 여러 종류의 연휴를 허용하고, 같은 종류의 같은 날짜만 막는다.
ALTER TABLE holiday
    ADD CONSTRAINT uk_holiday_holiday_kind_id_start_date UNIQUE (holiday_kind_id, start_date),
    DROP INDEX uk_holiday_start_date;
