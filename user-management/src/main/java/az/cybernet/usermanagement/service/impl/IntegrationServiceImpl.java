package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.client.IntegrationClient;
import az.cybernet.usermanagement.dto.request.OtpRequest;
import az.cybernet.usermanagement.service.abstraction.IntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IntegrationServiceImpl implements IntegrationService {

    private final IntegrationClient integrationClient;

    @Override
    public void sendOtp(String phone, String message) {
        Optional.ofNullable(integrationClient.sendOtp(new OtpRequest(phone, message)))
                .orElseThrow();
    }
}
