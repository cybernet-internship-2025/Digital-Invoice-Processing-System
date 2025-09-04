package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.client.IntegClient;
import az.cybernet.usermanagement.dto.request.OtpRequest;
import az.cybernet.usermanagement.service.abstraction.IntegService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IntegServiceImpl implements IntegService {
    private final IntegClient integrationClient;
    @Override
    public void sendOtp(String phone, String message) {
        integrationClient.sendOtp(new OtpRequest(phone, message));
    }
}
