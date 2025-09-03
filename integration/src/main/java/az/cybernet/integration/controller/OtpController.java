package az.cybernet.integration.controller;

import az.cybernet.integration.dto.OtpRequest;
import az.cybernet.integration.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/sms")
public class OtpController {

    private final SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendOtp(@RequestBody OtpRequest request) {
        smsService.sendOtp(request.getPhone(), request.getMessage());
        return ResponseEntity.ok().build();
    }
}
