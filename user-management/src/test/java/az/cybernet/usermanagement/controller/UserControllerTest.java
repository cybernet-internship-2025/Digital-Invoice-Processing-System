package az.cybernet.usermanagement.controller;

import az.cybernet.usermanagement.repository.UserRepository;
import az.cybernet.usermanagement.service.abstraction.UserService;
import az.cybernet.usermanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import az.cybernet.usermanagement.common.TestConstants;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static az.cybernet.usermanagement.common.TestConstants.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserServiceImpl userService;

    @MockBean
    UserRepository userRepository;


    @Test
    void testAddUser_ShouldReturnUserResponse() throws Exception {
        Mockito.when(userService.addUser(CREATE_USER_REQUEST)).thenReturn(USER_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/internal/users")
                       .header("adding user", "application/json"))
                .andExpect(status().isCreated());


    }
    @Test
    void testUpdateUser_ShouldReturnUserResponse() throws Exception {
        Mockito.when(userService.updateUser(UPDATE_USER_REQUEST)).thenReturn(USER_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/internal/users/")
                         .header("update user", "application/json"))
                .andExpect(status().isOk());
    }

}
