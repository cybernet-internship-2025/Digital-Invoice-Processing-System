package az.cybernet.invoice.dto.response.measurement;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MeasurementResponse {
    Long id;
    String name;
}