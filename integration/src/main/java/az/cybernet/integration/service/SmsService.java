package az.cybernet.integration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {

    public String sendOtp(String phone, String message) {
        return String.format("Sent SMS message with content \"%s\" to  %s", phone, message);
    }

}
