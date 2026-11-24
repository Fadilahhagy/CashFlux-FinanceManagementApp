
package com.cashflux.user.controller;

import com.cashflux.config.SecurityConfig;
import com.cashflux.security.JwtService;
import com.cashflux.user.dto.UpdateProfileRequest;
import com.cashflux.user.dto.UserResponse;
import com.cashflux.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldUpdateCurrentUserProfileSuccessfully() throws Exception {

        UserResponse response = new UserResponse(
                1L,
                "Updated User",
                "updated@example.com",
                OffsetDateTime.now()
        );

        when(userService.updateProfile(
                eq(1L),
                any(UpdateProfileRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/users/me")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                1L,
                                                null,
                                                List.of()
                                        )
                                ))
                                .contentType("application/json")
                                .content("""
                                        {
                                            "fullName": "Updated User",
                                            "email": "updated@example.com"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void shouldRejectUpdateProfileWithoutAuthentication() throws Exception {

        mockMvc.perform(
                        put("/api/v1/users/me")
                                .contentType("application/json")
                                .content("""
                                        {
                                            "fullName": "Updated User",
                                            "email": "updated@example.com"
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized());
    }
}