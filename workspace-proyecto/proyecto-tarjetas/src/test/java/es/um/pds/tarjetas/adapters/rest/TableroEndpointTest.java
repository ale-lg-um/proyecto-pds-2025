package es.um.pds.tarjetas.adapters.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;
import es.um.pds.tarjetas.domain.ports.input.ServicioTablero;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
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

public class TableroEndpointTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ServicioSesion servicioSesion;
    
    @MockBean
    private RepositorioTableros repositorioTableros;

    @MockBean 
    private ServicioTablero servicioTablero;
    
    @Test
    @DisplayName("Retornar 200 OK al obtener tableros por usuario")
    void obtener_RetornaOk() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        when(repositorioTableros.listarIdsPorUsuario(any()))
        						.thenReturn(List.of());

        mockMvc.perform(get("/tableros")
                .header("Authorization", "Bearer token-Válido"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Retornar 200 OK al obtener tablero por URL")
    void obtenerPorUrl_RetornaOk() throws Exception {
        Tablero tableroMock = mock(Tablero.class);
        
        when(tableroMock.getIdentificador())
        				.thenReturn(TableroId.of("1"));
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        when(repositorioTableros.buscarPorURL("urlValido"))
        						.thenReturn(Optional.of(tableroMock));

        mockMvc.perform(get("/tableros").param("url", "urlValido")
               .header("Authorization", "Bearer token-Válido"))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Retornar 404 NOT FOUND al obtener tablero por URL inexistente")
    void obtenerPorUrl_RetornaNotFound() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        when(repositorioTableros.buscarPorURL("urlInexistente"))
        						.thenReturn(Optional.empty());

        mockMvc.perform(get("/tableros").param("url", "urlInexistente")
               .header("Authorization", "Bearer token-Válido"))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Retornar 200 OK al obtener tablero por id")
    void obtenerPorId_RetornaOk() throws Exception {
        Tablero tableroMock = mock(Tablero.class);
        
        when(tableroMock.getIdentificador())
        				.thenReturn(TableroId.of("1"));
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        when(repositorioTableros.buscarPorId(any()))
        						.thenReturn(Optional.of(tableroMock));

        mockMvc.perform(get("/tableros/1")
               .header("Authorization", "Bearer token-Válido"))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Retornar 201 CREATED al crear un tablero")
    void crear_RetornaCreated() throws Exception {
        String body = "{\"nombre\":\"T1\", \"email\":\"prueba@um.es\"}";
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(post("/tableros")
               .header("Authorization", "Bearer token-Válido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al renombrar un tablero")
    void renombrar_RetornaNoContent() throws Exception {
        String body = "{\"nuevoNombre\":\"Nuevo\"}";
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(put("/tableros/1/renombrar")
               .header("Authorization", "Bearer token")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al eliminar un tablero")
    void eliminar_RetornaNoContent() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(delete("/tableros/1")
               .header("Authorization", "Bearer token-Válido"))
               .andExpect(status().isNoContent());
        
        
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al bloquear un tablero")
    void bloquear_RetornaNoContent() throws Exception {
    	String body = "{\"inicio\":\"2024-05-09T10:00:00\",\"fin\":\"2024-05-09T11:00:00\",\"motivo\":\"Bloqueo por el usuario\"}";
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(put("/tableros/1/bloquear")
               .header("Authorization", "Bearer token-Válido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al desbloquear un tablero")
    void desbloquear_RetornaNoContent() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(delete("/tableros/1/bloquear")
               .header("Authorization", "Bearer token-Válido"))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Retornar 200 OK al mostrar historial")
    void mostrarHistorial_RetornaOk() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(get("/tableros/1/historial")
               .header("Authorization", "Bearer token-Válido"))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Retornar 409 CONFLICT al fallar validación de token")
    void operacion_RetornaConflict() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
                		   .thenThrow(new IllegalArgumentException("Token inválido"));

        mockMvc.perform(get("/tableros/1")
               .header("Authorization", "Bearer token-Inválido"))
               .andExpect(status().isConflict())
               .andExpect(content().string("Token inválido"));
    }

}
	