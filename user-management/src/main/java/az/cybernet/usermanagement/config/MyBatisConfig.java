package az.cybernet.usermanagement.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("az.cybernet.usermanagement.mapper")
public class MyBatisConfig {
}
