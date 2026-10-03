CREATE TABLE admin_account (
    admin_account_id  BIGINT       NOT NULL AUTO_INCREMENT,
    login_id          VARCHAR(50)  NOT NULL,
    password          VARCHAR(255) NOT NULL,
    user_id           BIGINT       NOT NULL,
    created_at        DATETIME(6),
    updated_at        DATETIME(6),
    PRIMARY KEY (admin_account_id),
    UNIQUE KEY uk_admin_account_login_id (login_id),
    UNIQUE KEY uk_admin_account_user_id (user_id),
    CONSTRAINT fk_admin_account_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);
