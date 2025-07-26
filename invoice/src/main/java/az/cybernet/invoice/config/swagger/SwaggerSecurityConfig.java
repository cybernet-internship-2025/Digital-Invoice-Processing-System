package az.cybernet.invoice.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Digital Invoice Processing System API", version = "v1"),
        servers = @Server(url = "/", description = "Default Server URL"),
        security = @SecurityRequirement(name = "basicAuth")
)
public class SwaggerSecurityConfig {

}
