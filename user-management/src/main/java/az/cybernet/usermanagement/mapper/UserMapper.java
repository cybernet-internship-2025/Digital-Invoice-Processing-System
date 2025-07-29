package az.cybernet.usermanagement.mapper;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;



import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntityFromResponse(UserResponse userResponse);
//
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(null)")
    @Mapping(target = "isActive", constant = "true")
    UserEntity toUserEntityFromCreate(CreateUserRequest createUserRequest);

    UserEntity toUserEntityFromUpdate(UpdateUserRequest updateUserRequest);

    UserResponse toUserResponseFromEntity(UserEntity userEntity);

    List<UserResponse> toUserResponseList(List<UserEntity> entityList);
}
