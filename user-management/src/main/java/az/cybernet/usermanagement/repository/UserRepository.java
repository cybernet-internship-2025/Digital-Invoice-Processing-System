package az.cybernet.usermanagement.repository;

import az.cybernet.usermanagement.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;
@Mapper
public interface UserRepository {
    UserEntity addUser(UserEntity userEntity);
    UserEntity updateUser(UserEntity userEntity);
//    Optional<UserEntity> findByTaxId(Long taxId);
    List<UserEntity> findAll();
    Long findMaxTaxId();
    Optional<UserEntity> findById(Long id);
//    void deleteUser(Long id);
//    void restoreUser(long id);
}
