package com.ridemap.backend.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "RideMap API",
        version = "1.0.0",
        description = """
            REST API приложения RideMap.
            API предоставляет доступ к скейт-спотам,
            комментариям пользователей и связанным данным.
            """,
        contact = @Contact(
            name = "RideMap"
        )
    )
)

public class OpenApiConfig {
    
}
