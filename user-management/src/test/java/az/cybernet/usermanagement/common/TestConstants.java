package az.cybernet.usermanagement.common;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;

import java.time.LocalDateTime;

public interface TestConstants {

    UserEntity USER_ENTITY= UserEntity.builder()
            .name("Test")
            .id(3L)
            .taxId("0000000003")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(null)
            .build();

    CreateUserRequest CREATE_USER_REQUEST= CreateUserRequest.builder()
            .name("Test")
            .build();

    UserResponse USER_RESPONSE= UserResponse.builder()
            .name("Test")
            .id(3L)
            .taxId("0000000003")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(null)
            .build();
    UpdateUserRequest UPDATE_USER_REQUEST= UpdateUserRequest.builder()
            .name("Test")
            .taxId("0000000003")
            .build();

}
