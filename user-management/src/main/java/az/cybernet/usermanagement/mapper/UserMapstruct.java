package az.cybernet.usermanagement.mapper;

import az.cybernet.usermanagement.dto.request.UserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface UserMapstruct {
    UserResponse toUserResponseFromEntity(UserEntity userEntity);

    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    UserEntity toUserEntityFromCreate(UserRequest createUserRequest);

    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    void updateUserFromRequest(UserRequest request, @MappingTarget UserEntity userEntity);
}

