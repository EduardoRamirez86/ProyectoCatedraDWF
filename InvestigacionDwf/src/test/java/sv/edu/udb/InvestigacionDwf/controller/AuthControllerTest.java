package sv.edu.udb.InvestigacionDwf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.InvestigacionDwf.dto.request.LoginRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.RegisterRequest;
import sv.edu.udb.InvestigacionDwf.service.AuthService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private sv.edu.udb.InvestigacionDwf.security.jwt.JwtUtils jwtUtils;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void init() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPrimerNombre("Test");
        registerRequest.setPrimerApellido("User");
        registerRequest.setFechaNacimiento(java.time.LocalDate.of(2000, 1, 1));
        registerRequest.setDui("12345678-9");
        registerRequest.setTelefono("1234-5678");
        registerRequest.setDireccion("Calle 1");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("POST /auth/register - registro exitoso")
    void register_whenValidRequest_returnsToken() throws Exception {
        String expectedToken = "jwt-token";
        when(authService.register(registerRequest)).thenReturn(expectedToken);

        mockMvc.perform(post("/auth/register")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));
    }

    @Test
    @DisplayName("POST /auth/login - login exitoso")
    void login_whenValidRequest_returnsToken() throws Exception {
        String expectedToken = "jwt-token";
        when(authService.login(loginRequest)).thenReturn(expectedToken);

        mockMvc.perform(post("/auth/login")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));
    }
}
