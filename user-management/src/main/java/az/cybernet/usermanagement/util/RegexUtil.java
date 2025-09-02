package az.cybernet.usermanagement.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;
@Data
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RegexUtil {
       String AZERBAIJAN_PHONE_REGEX = "^(\\+994|0)?(50|51|55|70|77|99|10)\\d{7}$";
       String AZERBAIJAN_PIN_REGEX = "^[A-Z0-9]{7}$";

}
