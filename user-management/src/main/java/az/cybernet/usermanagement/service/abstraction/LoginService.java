package az.cybernet.usermanagement.service.abstraction;


public interface LoginService {

    boolean validateCitizen(String pin,String phoneNumber);
}
