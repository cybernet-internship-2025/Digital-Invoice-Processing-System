package az.cybernet.invoice.dto.request.login;

import lombok.Data;


@Data
public class LoginRequestDto {
    private String userId;
    private String password;
    private String taxId;
    private String phoneNumber;

    public boolean isValidPhone() {
        return phoneNumber != null &&
                phoneNumber.matches("^\\+994(50|51|55|70|77|99|10)\\d{7}$");
    }

    public boolean isValidUserIdOrTaxId() {
        boolean validUserId = userId != null && userId.matches("^[A-Z0-9]{7}$");
        boolean validTaxId  = taxId != null && taxId.matches("^[A-Z0-9]{6,10}$");
        return validUserId || validTaxId;
    }
}