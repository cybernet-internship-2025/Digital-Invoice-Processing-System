package az.cybernet.usermanagement.client;

import az.cybernet.usermanagement.dto.client.integration.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "integration", url = "${integration.service.url}")
public interface IntegrationClient {
    @GetMapping("/api/v1/iamas/{pin}")
    PersonDto getPersonByFin(@PathVariable("pin") String pin);
}
