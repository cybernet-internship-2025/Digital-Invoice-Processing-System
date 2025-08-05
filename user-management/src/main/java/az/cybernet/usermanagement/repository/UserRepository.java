package az.cybernet.usermanagement.repository;

import az.cybernet.usermanagement.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<UserEntity> findUserByTaxId(@Param("taxId") String taxId);

    void restoreUser(@Param("taxId") String taxId);

    void deleteUser(@Param("taxId") String taxId);

    void addUser(UserEntity userEntity);

    void updateUser(UserEntity userEntity);

    String findMaxTaxId();
}
