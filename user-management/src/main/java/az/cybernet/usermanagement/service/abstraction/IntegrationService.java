package az.cybernet.usermanagement.service.abstraction;

public interface IntegrationService {
    void sendOtp(String phone, String otp);
}
