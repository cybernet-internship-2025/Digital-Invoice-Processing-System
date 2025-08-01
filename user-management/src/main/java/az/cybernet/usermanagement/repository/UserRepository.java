package az.cybernet.usermanagement.repository;

import az.cybernet.usermanagement.entity.UserEntity;
import feign.Param;
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

    List<UserEntity> findAll(@Param("limit") Long limit);

    String findMaxTaxId();

}
