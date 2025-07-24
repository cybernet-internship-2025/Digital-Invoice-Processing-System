package az.cybernet.usermanagement.mapper;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserEntity toEntityFromResponse(UserResponse userResponse);
    UserResponse toUserResponse(UserEntity userEntity);
    UserEntity toUserEntity(CreateUserRequest createUserRequest);
    UserEntity toUserEntityFromUpdate(UpdateUserRequest updateUserRequest);


}
