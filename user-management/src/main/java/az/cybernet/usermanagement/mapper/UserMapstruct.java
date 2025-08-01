package az.cybernet.usermanagement.mapper;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapstruct {
    public abstract UserResponse toUserResponseFromEntity(UserEntity userEntity);

    public abstract UserEntity toUserEntityFromResponse(UserResponse userResponse);

    //    public abstract UserEntity toUserEntityFromResponse(UserResponse userResponse);
    public abstract UserEntity toUserEntityFromCreate(CreateUserRequest createUserRequest);
    public abstract UserEntity toUserEntityFromUpdate(UpdateUserRequest updateUserRequest);
    public abstract List<UserResponse> toUserResponseList(List<UserEntity> entityList);
}

