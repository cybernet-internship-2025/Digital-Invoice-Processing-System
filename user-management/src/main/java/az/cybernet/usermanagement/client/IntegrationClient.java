package az.cybernet.usermanagement.client;

import az.cybernet.usermanagement.dto.request.OtpRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import az.cybernet.usermanagement.dto.client.integration.PersonDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "integration", url = "http://localhost:8081")
public interface IntegrationClient {
    @PostMapping("/api/integration/v1/sms/send")
    String sendOtp(@RequestBody OtpRequest request);
    
    @GetMapping("/api/v1/iamas/{pin}")
    PersonDto getPersonByFin(@PathVariable("pin") String pin);
}
