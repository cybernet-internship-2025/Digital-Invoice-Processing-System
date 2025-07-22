package az.cybernet.invoice.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigInteger;
import java.sql.Timestamp;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {


    private BigInteger Id;
    private String name;
    private Timestamp createdAt;
    private Timestamp updateAt;
    private boolean isActive;


}
