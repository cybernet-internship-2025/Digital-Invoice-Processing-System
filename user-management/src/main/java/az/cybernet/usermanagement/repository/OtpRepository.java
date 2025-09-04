package az.cybernet.usermanagement.repository;

import az.cybernet.usermanagement.entity.FinLoginData;
import org.apache.ibatis.annotations.Param;

public interface OtpRepository {
    void insertOTPLoginData(@Param("loginData") FinLoginData loginData);

    void updateLoginOTPData(@Param("code") String code, @Param("pin") String pin, @Param("phoneNumber") String phoneNumber);
}
