package es.um.pds.tarjetas.adapters.rest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioAutenticacion;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;
import jakarta.transaction.Transactional;

//Levanta el contexto de la aplicación con test SpringBoot
@SpringBootTest
@AutoConfigureMockMvc
@Transactional

//Activa el perfil test de Spring, porque se pueden tener distintos entornos
@ActiveProfiles("test")

//Propiedades específicas para este test
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:tarjetasdb_test;DB_CLOSE_DELAY=-1",
		"spring.datasource.driverClassName=org.h2.Driver", "spring.datasource.username=sa",
		"spring.datasource.password=", "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false",
		"spring.mail.username=test@ejemplo.com" })

public class UsuarioEndpointTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ServicioAutenticacion servicioAutenticacion;
    
    @MockBean
    private ServicioSesion servicioSesion;
    
    @Test
    @DisplayName("Retornar error cuando el token es inválido o ha caducado")
    void realizarPeticion_ConTokenInvalido_RetornaError() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenThrow(new IllegalArgumentException("Token inválido o expirado"));
        
        mockMvc.perform(get("/tableros/1/listas")
        	   .header("Authorization", "Bearer token-Invalido"))
	           .andExpect(status().isConflict())
	           .andExpect(content().string("Token inválido o expirado"));
    }
    
    @Test
    @DisplayName("Retornar 200 OK al hacer login")
    void login_RetornaOk() throws Exception {
        mockMvc.perform(post("/usuarios/prueba@um.es/login"))
               .andExpect(status().isOk());

        verify(servicioAutenticacion).enviarCodigoLogin("prueba@um.es");
    }

    @Test
    @DisplayName("Retornar 200 OK al hacer verificar token")
    void verificarToken_RetornaOk() throws Exception {
        when(servicioAutenticacion.verificarCodigoLogin("prueba@um.es", "1234"))
                				  .thenReturn("token-Generado");

        mockMvc.perform(post("/usuarios/prueba@um.es/verificar")
               .param("codigo", "1234"))
               .andExpect(status().isOk())
               .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer token-Generado"))
               .andExpect(content().string("Login exitoso"));
    }

    @Test
    @DisplayName("Retornar 409 CONFLICT al hacer verificar token")
    void verificarToken_RetornaConflict() throws Exception {
        when(servicioAutenticacion.verificarCodigoLogin(anyString(), anyString()))
                				  .thenThrow(new IllegalArgumentException("Código incorrecto"));

        mockMvc.perform(post("/usuarios/prueba@um.es/verificar")
               .param("codigo", "erroneo"))
               .andExpect(status().isConflict())
               .andExpect(content().string("Código incorrecto"));
    }

    @Test
    @DisplayName("Retornar 200 OK al hacer validar y renovar")
    void validarYRenovar_RetornaOk() throws Exception {
        UsuarioId usuarioId = UsuarioId.of("prueba@um.es");
        
        when(servicioSesion.validarYRenovarToken("token-Valido"))
        				   .thenReturn(usuarioId);

        mockMvc.perform(get("/usuarios/validar")
               .header("Authorization", "Bearer token-Valido"))
               .andExpect(status().isOk())
               .andExpect(header().string("New-Token", "Bearer token-Valido"))
               .andExpect(jsonPath("$.correo").value("prueba@um.es"));
    }

    @Test
    @DisplayName("Retornar 409 CONFLICT al hacer validar y renovar")
    void validarYRenovar_RetornaConflict() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
                		   .thenThrow(new IllegalArgumentException("Token inválido"));

        mockMvc.perform(get("/usuarios/validar")
               .header("Authorization", "Bearer token-Incorrecto"))
               .andExpect(status().isConflict())
               .andExpect(content().string("Token inválido"));
    }

}
	