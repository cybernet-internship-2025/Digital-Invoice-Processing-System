package az.cybernet.integration.controller;

import az.cybernet.integration.dto.OtpRequest;
import az.cybernet.integration.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/sms")
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<String> sendSMS(@RequestBody OtpRequest request) {
        return ok(smsService.sendOtp(request.getPhone(), request.getMessage()));
    }
}
