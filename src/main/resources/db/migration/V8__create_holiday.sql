CREATE TABLE holiday (
    holiday_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (holiday_id),
    UNIQUE KEY uk_holiday_start_date (start_date)
);
