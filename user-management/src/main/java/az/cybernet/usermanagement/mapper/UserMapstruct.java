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
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    UserResponse toUserResponseFromEntity(UserEntity userEntity);

    @Mapping(target = "isActive", constant = "false")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    UserEntity toUserEntityFromCreate(UserRequest createUserRequest);

    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    void updateUserFromRequest(UserRequest request, @MappingTarget UserEntity userEntity);
}

