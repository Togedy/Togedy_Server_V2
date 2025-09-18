package com.togedy.togedy_server_v2.domain.global.support;

import com.togedy.togedy_server_v2.domain.global.config.DatabaseCleanerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(DatabaseCleanerExtension.class)
public class AbstractE2ETest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected FixtureSupport fixtureSupport;

    @Container
    @SuppressWarnings("resource")
    public static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("togedy_test")
            .withUsername("test")
            .withPassword("testpw")
            .withReuse(true);

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", MYSQL::getJdbcUrl);
        r.add("spring.datasource.username", MYSQL::getUsername);
        r.add("spring.datasource.password", MYSQL::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        r.add("spring.jpa.open-in-view", () -> "false");
        r.add("logging.level.org.hibernate.SQL", () -> "debug");
        r.add("logging.level.org.hibernate.orm.jdbc.bind", () -> "info");

        r.add("cloud.aws.s3.bucket", () -> "test-bucket");
        r.add("spring.cloud.aws.region.static", () -> "ap-northeast-2");
        r.add("spring.cloud.aws.credentials.access-key", () -> "dummy-access-key");
        r.add("spring.cloud.aws.credentials.secret-key", () -> "dummy-secret-key");
        r.add("spring.data.redis.host", () -> "localhost");
        r.add("spring.data.redis.port", () -> "6379");
        r.add("spring.data.redis.password", () -> "");
        r.add("swagger.server.url", () -> "http://localhost");
        r.add("jwt.secret-key", () -> "abcdefghijklmn123456789abcdefghijklmn123456789");
    }
}
