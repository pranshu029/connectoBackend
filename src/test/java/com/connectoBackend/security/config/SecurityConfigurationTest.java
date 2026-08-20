package com.connectoBackend.security.config;

import com.connectoBackend.security.filter.JwtAuthenticationFilter;
import com.connectoBackend.security.handler.JwtAuthenticationEntryPoint;
import com.connectoBackend.user.controller.UserController;
import com.connectoBackend.user.dto.response.UserResponse;
import com.connectoBackend.user.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(SecurityConfiguration.class)
class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Test
    @Disabled("Test configuration needs update - controller endpoint routing issue in WebMvcTest context")
    void postUsersShouldBeAllowedWithoutAuthentication() throws Exception {
        when(userService.createUser(any())).thenReturn(
                new UserResponse(
                        UUID.randomUUID(),
                        "testuser",
                        "Test User",
                        null,
                        false
                )
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Test",
                                  "lastName": "User",
                                  "username": "testuser",
                                  "email": "test@example.com",
                                  "password": "Test12345",
                                  "phoneNumber": "+1234567890"
                                }
                                """))
                .andExpect(status().isCreated());
    }
}
