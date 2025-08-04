package az.cybernet.usermanagement.service;

import az.cybernet.usermanagement.common.TestConstants;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.mapper.UserMapstruct;
import az.cybernet.usermanagement.repository.UserRepository;
import az.cybernet.usermanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static az.cybernet.usermanagement.common.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserMapstruct userMapstruct;

    @Test
    void test_addUser_ReturnSuccess() {
       // Arrange
        when(userMapstruct.toUserEntityFromCreate(CREATE_USER_REQUEST)).thenReturn(USER_ENTITY);
        willDoNothing().given(userRepository).addUser(USER_ENTITY);
        when(userMapstruct.toUserResponseFromEntity(USER_ENTITY)).thenReturn(USER_RESPONSE);

        //Act
        UserResponse result = userService.addUser(CREATE_USER_REQUEST);


        //Assert
        assertEquals(USER_RESPONSE, result);
        verify(userRepository).addUser(USER_ENTITY);

    }

    @Test
    void test_updateUser_ReturnSuccess() {

        //Arrange

    }


}
