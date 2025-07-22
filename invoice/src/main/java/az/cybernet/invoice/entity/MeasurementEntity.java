package az.cybernet.invoice.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import java.math.BigInteger;
import java.sql.Timestamp;

import static lombok.AccessLevel.PRIVATE;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class MeasurementEntity {
     BigInteger Id;
     String name;
     Timestamp createdAt;
     Timestamp updateAt;
     boolean isActive;
}
