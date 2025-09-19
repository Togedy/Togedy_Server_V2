package com.togedy.togedy_server_v2.domain.global.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.togedy.togedy_server_v2.domain.global.config.DatabaseCleanerExtension;
import com.togedy.togedy_server_v2.domain.global.factory.TestJwtFactory;
import com.togedy.togedy_server_v2.global.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DatabaseCleanerExtension.class)
public class AbstractE2ETest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected FixtureSupport fixtureSupport;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TestJwtFactory testJwtFactory;

    @MockitoBean
    protected S3Service s3Service;

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
    }

    @BeforeEach
    void setUp() {
        when(s3Service.uploadFile(any(MultipartFile.class)))
                .thenReturn("https://mock-s3/test.png");
    }
}
