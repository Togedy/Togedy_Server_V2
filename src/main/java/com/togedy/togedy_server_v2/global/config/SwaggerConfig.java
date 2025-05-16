package com.togedy.togedy_server_v2.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.server.url}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server();
        server.setUrl(serverUrl);

        return new OpenAPI()
                .servers(List.of(server))
                .components(new Components().addSecuritySchemes("AccessToken", new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                ))
                .addSecurityItem(new SecurityRequirement()
                        .addList("AccessToken"))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Togedy API")
                .description("투게디 API 문서")
                .version("2.0.0"); // API 버전
    }
}