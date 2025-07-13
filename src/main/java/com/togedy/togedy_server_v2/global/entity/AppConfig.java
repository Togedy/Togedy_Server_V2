package com.togedy.togedy_server_v2.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Getter
@Immutable
@Table(name = "app_config")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppConfig {

    @Id
    @Column(name = "config_key")
    private String configKey;

    @Column(name = "version_name")
    private String versionName;

    @Column(name = "version_code")
    private Long versionCode;

    @Column(name = "announcement")
    private String announcement;
}
