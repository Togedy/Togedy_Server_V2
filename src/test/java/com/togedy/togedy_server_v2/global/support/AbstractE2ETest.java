package com.togedy.togedy_server_v2.global.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.togedy.togedy_server_v2.global.db.DatabaseCleanerExtension;
import com.togedy.togedy_server_v2.global.factory.TestJwtFactory;
import com.togedy.togedy_server_v2.global.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DatabaseCleanerExtension.class)
public abstract class AbstractE2ETest extends AbstractInfraTest {

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

    @BeforeEach
    void setUp() {
        when(s3Service.uploadFile(any(MultipartFile.class)))
                .thenReturn("https://mock-s3/test.png");
    }
}
