package az.cybernet.invoice.repository;

import az.cybernet.invoice.dto.client.user.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserRepository {
    Optional<UserDto> findByUserIdOrTaxId(@Param("userId") String userId, @Param("taxId") String taxId);

    void update(UserDto user);

    void save(UserDto user);
}