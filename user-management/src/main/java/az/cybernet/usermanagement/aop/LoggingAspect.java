package az.cybernet.usermanagement.aop;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LoggingAspect {

}
