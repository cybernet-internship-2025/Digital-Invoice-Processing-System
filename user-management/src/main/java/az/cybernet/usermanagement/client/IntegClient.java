package az.cybernet.usermanagement.client;

import az.cybernet.usermanagement.dto.client.integration.PersonDto;
import az.cybernet.usermanagement.dto.request.OtpRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "integration", url = "http://localhost:8081")
public interface IntegClient {
    @GetMapping("/api/v1/iamas/{pin}")
    PersonDto getPersonByFin(@PathVariable("pin") String pin);

    @PostMapping("/api/integration/v1/sms/send")
    void sendOtp(@RequestBody OtpRequest request);
}