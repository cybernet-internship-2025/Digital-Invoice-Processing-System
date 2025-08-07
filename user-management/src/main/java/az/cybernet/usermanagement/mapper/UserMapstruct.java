package az.cybernet.usermanagement.mapper;

import az.cybernet.usermanagement.dto.request.UserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapstruct {
    public abstract UserResponse toUserResponseFromEntity(UserEntity userEntity);
    public abstract UserEntity toUserEntityFromCreate(UserRequest createUserRequest);
    public abstract List<UserResponse> toUserResponseList(List<UserEntity> entityList);

//    public abstract UserEntity toUserEntityFromResponse(UserResponse userResponse);
//    public abstract UserEntity toUserEntityFromUpdate(UpdateUserRequest updateUserRequest);
}

