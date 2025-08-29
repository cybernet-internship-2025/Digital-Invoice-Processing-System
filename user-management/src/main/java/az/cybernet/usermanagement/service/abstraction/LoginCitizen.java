package az.cybernet.usermanagement.service.abstraction;

import org.springframework.stereotype.Service;


public interface LoginCitizen {

    boolean validateCitizen(String pin,String phoneNumber);
}
