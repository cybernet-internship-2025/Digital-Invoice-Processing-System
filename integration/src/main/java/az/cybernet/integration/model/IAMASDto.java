package az.cybernet.integration.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IAMASDto {
    
    private String pin;
    private String passportSeries;
    private String passportNumber;
    private String citizenship;
    private String birthDate;
    private String patronymic;
    private String surname;
    private String name;
    private String birthPlace;
    private String address;
    private List<String> phoneNumbers;
}
