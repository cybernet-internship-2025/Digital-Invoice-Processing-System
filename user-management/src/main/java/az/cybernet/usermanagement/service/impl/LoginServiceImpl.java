package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.aop.annotation.Log;
import az.cybernet.usermanagement.client.IntegrationClient;
import az.cybernet.usermanagement.dto.client.integration.PersonDto;
import az.cybernet.usermanagement.service.abstraction.LoginService;
import az.cybernet.usermanagement.util.RegexUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Log
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LoginServiceImpl implements LoginService {
    IntegrationClient integrationClient;
    RegexUtil regexUtil;
    private static final String AZERBAIJAN_PHONE_REGEX = "^(\\+994|0)?(50|51|55|70|77|99|10)\\d{7}$";
    private static final String AZERBAIJAN_PIN_REGEX = "^[A-Z0-9]{7}$";

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
}
