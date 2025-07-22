package az.cybernet.invoice.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import java.math.BigInteger;
import java.sql.Timestamp;

import static lombok.AccessLevel.PRIVATE;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Measurement {


     BigInteger Id;
     String name;
     Timestamp createdAt;
     Timestamp updateAt;
     boolean isActive;


}
