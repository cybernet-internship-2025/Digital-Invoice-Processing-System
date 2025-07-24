package az.cybernet.invoice.client;

import az.cybernet.invoice.dto.client.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-management", url = "${user.service.url}")
public interface UserClient {
    @GetMapping("/api/users/by-tax-id/{taxId}")
    UserDto getUserByTaxId(@PathVariable("taxId") String taxId);
}
