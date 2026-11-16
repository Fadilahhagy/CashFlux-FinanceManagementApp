package com.cashflux.security;

import com.cashflux.config.SecurityConfig;
import com.cashflux.user.controller.UserController;
import com.cashflux.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.cashflux.user.dto.UserResponse;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class JwtAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {

        mockMvc.perform(
                        get("/api/v1/users/me")
                                .header("Authorization", "Bearer invalid-token")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnOkWhenTokenIsValid() throws Exception {

        when(jwtService.isTokenValid("valid-token"))
                .thenReturn(true);

        when(jwtService.extractUserId("valid-token"))
                .thenReturn(1L);

        when(userService.getUserById(1L))
                .thenReturn(new UserResponse(
                        1L,
                        "Fadilah",
                        "fadilah@example.com",
                        OffsetDateTime.now()
                ));

        mockMvc.perform(
                        get("/api/v1/users/me")
                                .header("Authorization", "Bearer valid-token")
                )
                .andExpect(status().isOk());
    }

}