package es.um.pds.tarjetas.adapters.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioLista;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
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

public class ListaEndpointTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ServicioSesion servicioSesion;
    
    @MockBean
    private RepositorioListas repositorioListas;
    
    @MockBean
    private ServicioLista servicioLista;
    
    @Test
    @DisplayName("Retornar error cuando el token es inválido o ha caducado")
    void realizarPeticion_ConTokenInvalido_RetornaError() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenThrow(new IllegalArgumentException("Token inválido o expirado"));

        
        mockMvc.perform(get("/tableros/tablero-1/listas")
        			   .header("Authorization", "Bearer token-Invalido"))
	            	   .andExpect(status().isConflict())
	            	   .andExpect(content().string("Token inválido o expirado"));
    }
    
    @Test
    @DisplayName("Retornar error cuando se intenta crear una lista con nombre vacío")
    void crearLista_ConNombreVacio_RetornaError() throws Exception {
        String body = "{\"nombre\": \"\"}";
        String email = "prueba@um.es";
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of(email));
        when(servicioLista.crearLista(eq("1"), eq(""), eq(email)))
        				   .thenThrow(new IllegalArgumentException("El nombre de la lista no puede estar vacío"));
        
        mockMvc.perform(post("/tableros/1/listas")
        			   .header("Authorization", "Bearer token-Valido")
        			   .contentType(MediaType.APPLICATION_JSON)
        			   .content(body))
	            	   .andExpect(status().isConflict())
	            	   .andExpect(content().string("El nombre de la lista no puede estar vacío"));
    }
    
    @Test
    @DisplayName("Retornar 200 OK cuando se intenta obtener listas de un tablero")
    void obtenerListas_RetornaOK() throws Exception {        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
    	when(repositorioListas.buscarPorTableroId(any(TableroId.class)))
    						  .thenReturn(Set.of());
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(get("/tableros/tablero-1/listas")
                .header("Authorization", "Bearer token-Valido"))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("Retornar error si se intenta obtener lista por ID que no existe")
    void obtenerListaPorId_NoExiste_RetornaNotFound() throws Exception {
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        when(repositorioListas.buscarPorId(any(ListaId.class)))
        				   	  .thenReturn(Optional.empty());

        mockMvc.perform(get("/listas/lista-1")
                .header("Authorization", "Bearer token-Valido"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Lista no encontrada."));
    }
    
    @Test
    @DisplayName("Retornar 201 CREATED cuando se crea una lista")
    void crearLista_RetornaCreated() throws Exception {
        String body = "{\"nombre\":\"Mi Nueva Lista\"}";
        ListaDTO listaCreada = new ListaDTO("id-1", "Mi Nueva Lista", false, 0, List.of(), Set.of());
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("user@um.es"));
        when(servicioLista.crearLista(eq("1"), eq("Mi Nueva Lista"), anyString()))
                		  .thenReturn(listaCreada);

        mockMvc.perform(post("/tableros/1/listas")
               .header("Authorization", "Bearer token-Valido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.nombre").value("Mi Nueva Lista"));
    }

	@Test
	@DisplayName("Retornar 204 NO CONTENT al eliminar una lista")
	void eliminarLista_RetornaNoContent() throws Exception {
	    when(servicioSesion.validarYRenovarToken(anyString()))
	    				   .thenReturn(UsuarioId.of("prueba@um.es"));
	
	    mockMvc.perform(delete("/tableros/1/listas/1")
	           .header("Authorization", "Bearer token-Valido"))
	           .andExpect(status().isNoContent());
	    
	    verify(servicioLista).eliminarLista("1", "1", "prueba@um.es");
	}
	
	@Test
	@DisplayName("Retornar 204 NO CONTENT al configurar el límite de una lista")
	void configurarLimite_RetornaNoContent() throws Exception {
	    String body = "{\"limite\":5}";
	    
	    when(servicioSesion.validarYRenovarToken(anyString()))
	    				   .thenReturn(UsuarioId.of("prueba@um.es"));
	
	    mockMvc.perform(put("/tableros/1/listas/1/configurar")
	           .header("Authorization", "Bearer token-Valido")
	           .contentType(MediaType.APPLICATION_JSON)
	           .content(body))
	           .andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Retornar error si al renombrar lista el servicio lanza excepción")
	void renombrarLista_ErrorNegocio_RetornaConflict() throws Exception {
	    String body = "{\"nuevoNombre\":\"Nombre Duplicado\"}";
	    
	    when(servicioSesion.validarYRenovarToken(anyString()))
	    				   .thenReturn(UsuarioId.of("prueba@um.es"));
	    
	    doThrow(new IllegalArgumentException("Ya existe una lista con ese nombre"))
	        			   .when(servicioLista).renombrarLista(anyString(), anyString(), anyString(), anyString());
	
	    mockMvc.perform(put("/tableros/1/listas/1/renombrar")
	           .header("Authorization", "Bearer token-Valido")
	           .contentType(MediaType.APPLICATION_JSON)
	           .content(body))
	           .andExpect(status().isConflict())
	           .andExpect(content().string("Ya existe una lista con ese nombre"));
	}
	
    @Test
    @DisplayName("Retornar 204 NO CONTENT al renombrar una lista")
    void renombrar_RetornaNoContent() throws Exception {
        String body = "{\"nuevoNombre\":\"Nuevo Nombre\"}";
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(put("/tableros/1/listas/1/renombrar")
               .header("Authorization", "Bearer token-Valido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isNoContent());

        verify(servicioLista).renombrarLista("1", "1", "Nuevo Nombre", "prueba@um.es");
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al eliminar una lista")
    void eliminar_RetornaNoContent() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(delete("/tableros/1/listas/1")
               .header("Authorization", "Bearer token-Valido"))
               .andExpect(status().isNoContent());

        verify(servicioLista).eliminarLista("1", "1", "prueba@um.es");
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al definir una lista como especial")
    void definirEspecial_RetornaNoContent() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(put("/tableros/1/listas/1/especial")
               .header("Authorization", "Bearer token"))
               .andExpect(status().isNoContent());

        verify(servicioLista).definirListaEspecial("1", "1", "prueba@um.es");
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al configurar el límite de una lista")
    void configurarLimite_RetornaNOContent() throws Exception {
        String body = "{\"limite\":10}";
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(put("/tableros/1/listas/1/configurar")
               .header("Authorization", "Bearer token")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isNoContent());

        verify(servicioLista).configurarLimiteLista("1", "1", 10, "prueba@um.es");
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al configurar prerrequisitos de una lista")
    void configurarPrerrequisitos_RetornaNoContent() throws Exception {
        String body = "{\"prerrequisitos\":[\"2\", \"3\"]}";
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(put("/tableros/1/listas/1/prerrequisitos")
               .header("Authorization", "Bearer token-Valido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isNoContent());

        verify(servicioLista).configurarPrerrequisitosLista(eq("1"), eq("1"), anySet(), eq("prueba@um.es"));
    }

    @Test
    @DisplayName("Retornar 409 CONFLICT al fallar una validación de negocio en cualquier acción")
    void operacion_ErrorNegocio_RetornaConflict() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString())).thenReturn(UsuarioId.of("prueba@um.es"));
        
        doThrow(new IllegalArgumentException("Error de validación"))
            				.when(servicioLista).eliminarLista(anyString(), anyString(), anyString());

        mockMvc.perform(delete("/tableros/1/listas/1")
               .header("Authorization", "Bearer token-Valido"))
               .andExpect(status().isConflict())
               .andExpect(content().string("Error de validación"));
    }

}
	