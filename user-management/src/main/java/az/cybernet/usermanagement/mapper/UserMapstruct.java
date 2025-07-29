package az.cybernet.usermanagement.mapper;

import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapstruct32 {
    UserResponse toUserResponseFromEntity(UserEntity userEntity);
}


//    UserEntity toEntityFromResponse(UserResponse userResponse);
//    UserEntity toUserEntity(CreateUserRequest createUserRequest);
//    UserEntity toUserEntityFromUpdate(UpdateUserRequest updateUserRequest);