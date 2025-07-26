package az.cybernet.invoice.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .sessionManagement(configure -> configure.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/DIPS/swagger-ui.html",
                                "/api/DIPS/swagger-ui/**",
                                "/api/DIPS/api-docs/**")
                        .authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));

        return http.build();
    }
}

