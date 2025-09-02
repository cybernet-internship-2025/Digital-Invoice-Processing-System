package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.dto.request.EnterCodeByTelegramRequest;
import az.cybernet.usermanagement.dto.request.LoginByEmailRequest;
import az.cybernet.usermanagement.dto.request.LoginByTelegramRequest;
import az.cybernet.usermanagement.dto.request.LoginRequest;
import az.cybernet.usermanagement.entity.LoginAttempt;
import az.cybernet.usermanagement.entity.Otp;
import az.cybernet.usermanagement.entity.RecentLoginAttempts;
import az.cybernet.usermanagement.exception.OtpLimitExceededException;
import az.cybernet.usermanagement.exception.RedisOperationException;
import az.cybernet.usermanagement.exception.VerificationCodeException;
import az.cybernet.usermanagement.service.abstraction.IntegrationService;
import az.cybernet.usermanagement.service.abstraction.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import az.cybernet.usermanagement.aop.annotation.Log;
import az.cybernet.usermanagement.client.IntegrationClient;
import az.cybernet.usermanagement.dto.client.integration.PersonDto;
import az.cybernet.usermanagement.util.RegexUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults
import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@Slf4j
@Log
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LoginServiceImpl implements LoginService {
    private final IntegrationService integrationService;
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final RedisTemplate<String, RecentLoginAttempts> attemptsRedisTemplate;
    private final RedisTemplate<String, Otp> otpRedisTemplate;
    private final LoginHelper loginHelper;
    private final JavaMailSender mailSender;
    IntegrationClient integrationClient;
    RegexUtil regexUtil;
    private static final String AZERBAIJAN_PHONE_REGEX = "^(\\+994|0)?(50|51|55|70|77|99|10)\\d{7}$";
    private static final String AZERBAIJAN_PIN_REGEX = "^[A-Z0-9]{7}$";

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String loginByPhone(LoginRequest loginRequest) {
        String phoneNumber = loginRequest.getPhoneNumber();
        //todo pin və phoneNumber must check
        String otp = String.valueOf(this.generateOtp());
        redisTemplate.opsForValue().set("OTP_" + phoneNumber, otp, 5, TimeUnit.MINUTES);
        integrationService.sendOtp(phoneNumber, otp);
        return "OTP sent: " + phoneNumber;
    }

    @Override
    public String loginByEmail(LoginByEmailRequest loginByEmailRequest) {
        String email = loginByEmailRequest.getEmail();
        String otp = String.valueOf(this.generateOtp());
        redisTemplate.opsForValue().set("OTP_" + email, otp, 5, TimeUnit.MINUTES);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Sizin OTP kodunuz");
        message.setText("Sizin OTP kodunuz: " + otp);
        mailSender.send(message);
        return "OTP sent: " + email;
    }

    public int generateOtp() {
        SecureRandom random = new SecureRandom();
        return 100000 + random.nextInt(900000);
    }

    @Override
    public String loginByTelegram(LoginByTelegramRequest request) {
        checkAttemptsLimit(request);
        String verificationCode = generateOtpAndSaveAtRedis(request);

        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("chat_id", request.getChatId());
            params.add("text", verificationCode);

            restTemplate.postForObject(url, params, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }

        loginHelper.insertOTPLoginData(request, verificationCode);
        return "OTP sent";
    }

    private void checkAttemptsLimit(LoginByTelegramRequest request) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        String loginByFinCacheKey = getLoginByFinCacheKey(request.getPin());

        if (hasKeyInRedis(loginByFinCacheKey)) {
            RecentLoginAttempts recentLoginAttempts = attemptsRedisTemplate.opsForValue().get(loginByFinCacheKey);

            if (Objects.isNull(recentLoginAttempts)) {
                log.warn("Recent login attempts is null for request {}", request);
                recentLoginAttempts = new RecentLoginAttempts(new ArrayList<>());
            }

            recentLoginAttempts.getLoginAttempts().removeIf(attempt -> LocalDateTime
                    .parse(attempt.getDate()).isBefore(oneHourAgo));

            if (recentLoginAttempts.getLoginAttempts().size() > 2) {
                log.error("User trying to send too many otp {}", request);
                throw new OtpLimitExceededException("Too many OTP requests in 1 hour");
            }
        }
    }

    private String generateOtpAndSaveAtRedis(LoginByTelegramRequest request) {
        Otp otp = new Otp(String.valueOf(generateOtp()), 0);
        String loginByFinCacheKey = getLoginByFinCacheKey(request.getPin());

        RecentLoginAttempts recentLoginAttempts = new RecentLoginAttempts(new ArrayList<>());
        otpRedisTemplate.opsForValue().set(getOtpCodeCacheKey(request.getPin()), otp, 5, TimeUnit.MINUTES);

        if (hasKeyInRedis(loginByFinCacheKey)) {
            recentLoginAttempts = attemptsRedisTemplate.opsForValue().get(loginByFinCacheKey);
        }

        if (Objects.isNull(recentLoginAttempts) || Objects.isNull(recentLoginAttempts.getLoginAttempts())) {
            throw new RedisOperationException("Failed to retrieve login attempts data from Redis");
        }

        recentLoginAttempts.getLoginAttempts().add(new LoginAttempt(LocalDateTime.now().toString()));

        attemptsRedisTemplate.opsForValue().set(loginByFinCacheKey, recentLoginAttempts, 5, TimeUnit.MINUTES);

        return otp.getVerificationCode();
    }

    private String getLoginByFinCacheKey(String pin) {
        return "loginByFinCacheKey-" + pin;
    }

    private String getOtpCodeCacheKey(String fin) {
        return "otpCodeCacheKey-" + fin;
    }

    public boolean hasKeyInRedis(String key) {
        try {
            return attemptsRedisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Redis error while checking key {}: {}", key, e.getMessage(), e);
            throw new RedisOperationException("Redis error while checking key: " + key, e);
        }
    }

    @Override
    public boolean checkIsVerificationCodeSuccess(EnterCodeByTelegramRequest request) {
        log.info("Enter code request {} ", request);
        String otpCacheKey = getOtpCodeCacheKey(request.getPin());

        if (hasKeyInRedis(otpCacheKey)) {
            Otp otp = otpRedisTemplate.opsForValue().get(otpCacheKey);

            if (Objects.isNull(otp)) {
                log.error("Otp code not found at redis");
                throw new VerificationCodeException("OTP expired or not found", 400);
            }
            if (otp.getAttemptCount() > 2) {
                log.error("User has entered the OTP code incorrectly more than 3 times");
                throw new VerificationCodeException("OTP attempt limit reached", 400);
            }
            if (!otp.getVerificationCode().equals(request.getCode())) {
                otp.setAttemptCount(otp.getAttemptCount() + 1);
                otpRedisTemplate.opsForValue().set(otpCacheKey, otp, 1, TimeUnit.HOURS);
                log.error("User has entered the OTP code incorrect");
                throw new VerificationCodeException("Wrong OTP code", 400);
            }
        } else {
            log.error("OTP not found in Redis, possibly expired");
            throw new VerificationCodeException("OTP expired or not found", 400);
        }
       // loginHelper.updateLoginOTPData(request.getCode(), request.getPin(), request.getPhoneNumber());
        return true;
      
      @Override
    public boolean validateCitizen(String pin, String phoneNumber) {
        if (!isValidPin(pin)) {
            throw new IllegalArgumentException("Fin yanlışdır. Zəhmətolmasa bir daha yoxlayın .");
        }

        if (!isValidAzerbaijanPhone(phoneNumber)) {
            throw new IllegalArgumentException("Nömrə yanlışdır. Zəhmətolmasa bir daha yoxlayın .");
        }

        try {
            PersonDto personDto = integrationClient.getPersonByFin(pin);
            return personDto.getPhoneNumbers().contains(phoneNumber);

        } catch (Exception e) {
            throw new RuntimeException("Xəta baş verdi",e);
        }

    }
    private boolean isValidAzerbaijanPhone(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches(regexUtil.getAZERBAIJAN_PHONE_REGEX());
    }

    private boolean isValidPin(String pin) {

        return pin != null && pin.matches(regexUtil.getAZERBAIJAN_PIN_REGEX());

    }
}}

