package com.chatop.api;

import com.chatop.api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.chatop.api.service.UserService;

import java.util.Collections;
import java.util.Optional;
import org.mockito.Mockito;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class ChatopdbApplicationTests {

	// Simule des requêtes HTTP
@Autowired
private MockMvc mockMvc;

	// Mock du service
@MockBean
private UserService userService;

@BeforeEach
void setUp(){
	// Prépare un utilisateur fictif pour les tests
	User mockUser = new User();
	mockUser.setId(1L);
	mockUser.setName("TOTO");

	when(userService.getUser(1L)).thenReturn(Optional.of(mockUser));
}

	@Test
	public void testGetUserById() throws Exception {
	mockMvc.perform(get("/auth/me"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is ("TOTO")));
	}

}
