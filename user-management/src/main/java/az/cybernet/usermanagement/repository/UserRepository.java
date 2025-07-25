package az.cybernet.usermanagement.repository;

import az.cybernet.usermanagement.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserRepository {
    Optional<UserEntity> findUserByTaxId(String taxId);
    void restoreUser(Long id);
    void deleteUser(Long id);
}
