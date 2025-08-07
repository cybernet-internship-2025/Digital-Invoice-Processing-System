package az.cybernet.invoice.dto.request.measurement;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeasurementRequest {
    @NotBlank(message = "Measurement name cannot be blank")
    String name;
}

