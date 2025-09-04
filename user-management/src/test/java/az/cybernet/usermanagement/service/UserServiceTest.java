package az.cybernet.usermanagement.service;
import az.cybernet.usermanagement.dto.request.UserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;

import az.cybernet.usermanagement.entity.UserEntity;
import az.cybernet.usermanagement.exception.ExceptionConstants;
import az.cybernet.usermanagement.exception.InvalidTaxIdException;
import az.cybernet.usermanagement.exception.NotFoundException;
import az.cybernet.usermanagement.mapper.UserMapstruct;
import az.cybernet.usermanagement.repository.UserRepository;
import az.cybernet.usermanagement.service.abstraction.RegistrationService;
import az.cybernet.usermanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;
    RegistrationService registrationService;

    @Mock
    UserMapstruct userMapstruct;

    String taxId ="0000000003";

    UserEntity USER_ENTITY= UserEntity.builder()
            .name("Test")
            .id(3L)
            .taxId("0000000003")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(null)
            .build();

    UserRequest CREATE_USER_REQUEST= UserRequest.builder()
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
    UserRequest UPDATE_USER_REQUEST= UserRequest.builder()
            .name("Test")
            .build();


    @Test
    void test_addUser_ReturnSuccess() {
       // Arrange
        when(userMapstruct.toUserEntityFromCreate(CREATE_USER_REQUEST)).thenReturn(USER_ENTITY);
        willDoNothing().given(userRepository).addUser(USER_ENTITY);
        when(userMapstruct.toUserResponseFromEntity(USER_ENTITY)).thenReturn(USER_RESPONSE);

        //Act
        UserResponse result = registrationService.addUser(CREATE_USER_REQUEST);

        //Assert
        assertEquals(USER_RESPONSE, result);
        verify(userRepository).addUser(USER_ENTITY);

    }

    @Test
    void test_updateUser_ReturnSuccess() {
        //Arrange

        String taxId ="0000000003";

        UserEntity USER_ENTITY= UserEntity.builder()
                .name("Test")
                .id(3L)
                .taxId("0000000003")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

        UserRequest CREATE_USER_REQUEST= UserRequest.builder()
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
        UserRequest UPDATE_USER_REQUEST= UserRequest.builder()
                .name("Test")
                .build();

        when(userRepository.findUserByTaxId(taxId)).thenReturn(Optional.of(USER_ENTITY));
        USER_ENTITY.setUpdatedAt(LocalDateTime.now());
        USER_ENTITY.setName(UPDATE_USER_REQUEST.getName());
        willDoNothing().given(userRepository).updateUser(USER_ENTITY);
        when(userMapstruct.toUserResponseFromEntity(USER_ENTITY)).thenReturn(USER_RESPONSE);

        UserResponse actualResponse = userService.updateUser(USER_ENTITY.getTaxId(),UPDATE_USER_REQUEST);

        assertEquals(USER_RESPONSE, actualResponse);
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals("Test", actualResponse.getName());
        Assertions.assertEquals(taxId, actualResponse.getTaxId());

        verify(userRepository).updateUser(USER_ENTITY);

    }
    @Test
    void Should_ThrowException_When_TaxId_NotFound() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
            userService.findUserByTaxId("0000000003");
        });

          Assertions.assertEquals(ExceptionConstants.USER_NOT_FOUND.getMessage(), exception.getMessage());
          Assertions.assertEquals(ExceptionConstants.USER_NOT_FOUND.getCode(), exception.getCode());

    }

    @Test
    void test_findUserByTaxId_ReturnSuccess() {
        when(userRepository.findUserByTaxId(taxId)).thenReturn(Optional.of(USER_ENTITY));
        when(userMapstruct.toUserResponseFromEntity(USER_ENTITY)).thenReturn(USER_RESPONSE);

        UserResponse result = userService.findUserByTaxId(taxId);

        assertEquals(USER_RESPONSE, result);
        verify(userRepository).findUserByTaxId(taxId);
        verify(userMapstruct).toUserResponseFromEntity(USER_ENTITY);
    }

    @Test
    void test_restoreUser_ReturnSuccess() {
        when(userRepository.findUserByTaxId(taxId)).thenReturn(Optional.of(USER_ENTITY));
        willDoNothing().given(userRepository).restoreUser(taxId);

        userService.restoreUser(taxId);

        verify(userRepository).findUserByTaxId(taxId);
        verify(userRepository).restoreUser(taxId);
    }

    @Test
    void test_deleteUser_ReturnSuccess() {
        when(userRepository.findUserByTaxId(taxId)).thenReturn(Optional.of(USER_ENTITY));
        willDoNothing().given(userRepository).deleteUser(taxId);

        userService.deleteUser(taxId);

        verify(userRepository).findUserByTaxId(taxId);
        verify(userRepository).deleteUser(taxId);
    }

    @Test
    void test_addUser_ShouldThrowException_WhenTaxIdNotValid() {
        when(userMapstruct.toUserEntityFromCreate(CREATE_USER_REQUEST)).thenReturn(USER_ENTITY);
        when(userRepository.findMaxTaxId()).thenReturn("INVALID_TAX_ID");

        InvalidTaxIdException exception = Assertions.assertThrows(InvalidTaxIdException.class, () -> {
            registrationService.addUser(CREATE_USER_REQUEST);
        });

        assertEquals(ExceptionConstants.INVALID_TAX_ID_EXCEPTION.getCode(), exception.getCode());
        assertEquals(ExceptionConstants.INVALID_TAX_ID_EXCEPTION.getMessage(), exception.getMessage());
    }

    @Test
    void test_addUser_ShouldSetGeneratedTaxId() {
        when(userMapstruct.toUserEntityFromCreate(CREATE_USER_REQUEST)).thenReturn(USER_ENTITY);
        when(userRepository.findMaxTaxId()).thenReturn("0000000005");
        willDoNothing().given(userRepository).addUser(USER_ENTITY);
        when(userMapstruct.toUserResponseFromEntity(USER_ENTITY)).thenReturn(USER_RESPONSE);

        registrationService.addUser(CREATE_USER_REQUEST);

        Assertions.assertEquals("0000000006", USER_ENTITY.getTaxId());
    }

    @Test
    void test_addUser_WhenFindMaxTaxIdIsNull_ShouldStartFromOne() {
        when(userMapstruct.toUserEntityFromCreate(CREATE_USER_REQUEST)).thenReturn(USER_ENTITY);
        when(userRepository.findMaxTaxId()).thenReturn(null);
        willDoNothing().given(userRepository).addUser(any(UserEntity.class));
        when(userMapstruct.toUserResponseFromEntity(any(UserEntity.class))).thenReturn(USER_RESPONSE);

        UserResponse result = registrationService.addUser(CREATE_USER_REQUEST);

        assertEquals(USER_RESPONSE, result);
        verify(userRepository).addUser(any(UserEntity.class));
    }

    @Test
    void test_updateUser_ShouldSetUpdatedAtField() {
        when(userRepository.findUserByTaxId(taxId)).thenReturn(Optional.of(USER_ENTITY));
        willDoNothing().given(userRepository).updateUser(USER_ENTITY);
        when(userMapstruct.toUserResponseFromEntity(USER_ENTITY)).thenReturn(USER_RESPONSE);

        LocalDateTime beforeUpdate = LocalDateTime.now();
        userService.updateUser(taxId, UPDATE_USER_REQUEST);
        LocalDateTime afterUpdate = USER_ENTITY.getUpdatedAt();

        Assertions.assertNotNull(afterUpdate);
        Assertions.assertTrue(afterUpdate.isAfter(beforeUpdate));
    }

}
