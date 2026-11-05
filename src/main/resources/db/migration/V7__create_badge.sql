CREATE TABLE badge (
    badge_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    detail VARCHAR(255),
    type VARCHAR(30) NOT NULL,
    period_key VARCHAR(20) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (badge_id),
    UNIQUE KEY uk_badge_user_id_type_period_key (user_id, type, period_key)
);