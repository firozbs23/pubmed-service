package com.omnizia.pubmedservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Pubmed Service API")
                .version("1.0")
                .description("This API is used to collect publications from pubmed database"))
        .servers(
            List.of(
                new Server().url("https://pubmed-service.omnizia.com").description("Dev server")));
  }
}
