package az.cybernet.usermanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("az.cybernet.usermanagement.repository")
public class UserManagement {

    public static void main(String[] args) {
        SpringApplication.run(UserManagement.class, args);
    }
}
