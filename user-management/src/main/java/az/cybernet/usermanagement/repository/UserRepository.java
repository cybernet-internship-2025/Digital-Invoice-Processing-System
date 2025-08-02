package az.cybernet.usermanagement.repository;

import az.cybernet.usermanagement.entity.UserEntity;
import feign.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {

    Optional<UserEntity> findUserByTaxId(String taxId);

    void restoreUser(Long id);

    void deleteUser(Long id);

    void addUser(UserEntity userEntity);

    void updateUser(UserEntity userEntity);

    List<UserEntity> findAll(@Param("limit") Long limit);

    Long findMaxTaxId();
}
