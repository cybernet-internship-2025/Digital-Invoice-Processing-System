package az.cybernet.usermanagement.client;

import az.cybernet.usermanagement.dto.client.integration.IAMASDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "integration", url = "${integration.service.url}")
public interface IntegrationClient {
    @GetMapping("/api/v1/iamas/{pin}")
    IAMASDto getPinData(@PathVariable("pin") String pin);
}
