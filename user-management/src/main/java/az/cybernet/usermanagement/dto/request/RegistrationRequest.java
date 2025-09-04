package az.cybernet.usermanagement.dto.request;

import az.cybernet.usermanagement.enums.RegistrationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationRequest {
    RegistrationType type;
//    @NotBlank(message = "Legal address cannot be blank")
//    String legalAddress;
//    @NotBlank(message = "Registration address cannot be blank")
//    String registrationAddress;
//    @NotBlank(message = "Phone number cannot be blank")
//    String phoneNumber;
//    @NotBlank(message = "Legal name cannot be blank")
//    String legalEntityName;
}
