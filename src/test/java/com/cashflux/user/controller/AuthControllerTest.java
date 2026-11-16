package com.cashflux.user.controller;

import com.cashflux.config.SecurityConfig;
import com.cashflux.security.JwtService;
import com.cashflux.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldAllowAccessToRegisterWithoutToken() throws Exception {

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAllowAccessToLoginWithoutToken() throws Exception {

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(status().isBadRequest());
    }
}