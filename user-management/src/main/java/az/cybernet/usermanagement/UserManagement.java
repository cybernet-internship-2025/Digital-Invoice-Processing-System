package az.cybernet.usermanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("az.cybernet.usermanagement.repository")
@EnableCaching
@EnableFeignClients(basePackages = "az.cybernet.usermanagement.client")
@EnableConfigurationProperties
public class UserManagement {
    public static void main(String[] args) {
        SpringApplication.run(UserManagement.class, args);
    }
}
