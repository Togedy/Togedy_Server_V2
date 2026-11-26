CREATE TABLE holiday_kind (
    holiday_kind_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    badge_image_url VARCHAR(255),
    badge_enabled BIT(1) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (holiday_kind_id),
    UNIQUE KEY uk_holiday_kind_name (name)
);

INSERT INTO holiday_kind (name, badge_image_url, badge_enabled, created_at, updated_at)
VALUES ('설날', NULL, b'0', NOW(6), NOW(6)),
       ('추석', NULL, b'0', NOW(6), NOW(6)),
       ('삼일절', NULL, b'0', NOW(6), NOW(6)),
       ('어린이날', NULL, b'0', NOW(6), NOW(6)),
       ('크리스마스', NULL, b'0', NOW(6), NOW(6));

ALTER TABLE holiday ADD COLUMN holiday_kind_id BIGINT NULL AFTER holiday_id;

UPDATE holiday h
    JOIN holiday_kind hk ON h.name LIKE CONCAT('%', hk.name, '%')
SET h.holiday_kind_id = hk.holiday_kind_id;

ALTER TABLE holiday
    MODIFY COLUMN holiday_kind_id BIGINT NOT NULL,
    DROP COLUMN name,
    ADD CONSTRAINT fk_holiday_holiday_kind FOREIGN KEY (holiday_kind_id) REFERENCES holiday_kind (holiday_kind_id);
