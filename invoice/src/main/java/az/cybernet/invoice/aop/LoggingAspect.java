package az.cybernet.invoice.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LoggingAspect {
    ObjectMapper objectMapper;

    @Pointcut("within(@az.cybernet.invoice.aop.annotation.Log *) && !@annotation(az.cybernet.invoice.aop.annotation.LogIgnore)")
    public void loggingPointCut() {
    }

    @SneakyThrows
    @Around(value = "loggingPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        var signature = ((MethodSignature) joinPoint.getSignature());
        var parameters = objectMapper.writeValueAsString(joinPoint.getArgs());
        logEvent("start", signature, parameters);
        Object response;

        try {
            response = joinPoint.proceed();
        } catch (Throwable throwable) {
            logEvent("error", signature, parameters);
            throw throwable;
        }
        logEndAction(signature, response);
        return response;
    }

    private void logEvent(String eventName, MethodSignature signature, String parameters) {
        log.info("ActionLog.{}.{} {}", signature.getName(), eventName, redactCredentials(parameters));
    }

    private void logEndAction(MethodSignature signature, Object response) {
        if (void.class.equals(signature.getReturnType())) log.info("ActionLog.{}.end", signature.getName());
        else log.info("ActionLog.{}.end {}", signature.getName(), redactResponse(response));
    }

    private String redactCredentials(String input) {
        String[] sensitiveFields = {"username", "password", "email", "accessToken", "refreshToken"};
        for (String field : sensitiveFields) {
            String regex = String.format("\"%s\":\"(.*?)\"", field);
            input = input.replaceAll(regex, "\"" + field + "\":\"********\"");
        }
        return input;
    }

    private String redactResponse(Object response) {
        if (response != null) {
            try {
                String responseJson = objectMapper.writeValueAsString(response);
                return redactCredentials(responseJson);
            } catch (Exception e) {
                log.error("Error redacting response JSON", e);
            }
        }
        return String.valueOf(response);
    }
}