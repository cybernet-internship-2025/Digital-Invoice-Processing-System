package az.cybernet.usermanagement.mapper;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntityFromResponse(UserResponse userResponse);
    UserEntity toUserEntity(CreateUserRequest createUserRequest);
    UserEntity toUserEntityFromUpdate(UpdateUserRequest updateUserRequest);
    UserResponse toUserResponseFromEntity(UserEntity userEntity);
}
