package com.chatop.api.controller;

import com.chatop.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Test
    public void testGetUsers() throws Exception{
        // Mock du service pour renvoyer une liste vide d'utilisateurs
        Mockito.when(userService.getUsers()).thenReturn(Collections.emptyList());

        // Test de la requête GET /api/users (à cause de @RequestMapping("/api"))
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk());// Vérifie que le statut HTTP est 200 OK
    }
}
