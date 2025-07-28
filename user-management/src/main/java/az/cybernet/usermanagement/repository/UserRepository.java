package az.cybernet.usermanagement.repository;

import az.cybernet.usermanagement.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Mapper
@Repository
public interface UserRepository {
    Optional<UserEntity> findUserByTaxId(String taxId);
    void restoreUser(Long id);
    void deleteUser(Long id);
}
