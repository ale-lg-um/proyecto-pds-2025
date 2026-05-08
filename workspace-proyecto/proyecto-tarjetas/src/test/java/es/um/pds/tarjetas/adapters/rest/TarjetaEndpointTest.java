package es.um.pds.tarjetas.adapters.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioPlantilla;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;
import es.um.pds.tarjetas.domain.ports.input.ServicioTarjeta;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;
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

public class TarjetaEndpointTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ServicioSesion servicioSesion;
    
    @MockBean
    private ServicioPlantilla servicioPlantilla;
    
    @MockBean
    private RepositorioTarjetas repositorioTarjetas;
    
    @MockBean
    private ServicioTarjeta servicioTarjeta;
    
    @Test
    @DisplayName("Retornar 200 OK al obtener tarjetas por lista")
    void obtenerPorLista_RetornaOk() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        when(repositorioTarjetas.buscarPorListaId(any()))
        						.thenReturn(List.of());

        mockMvc.perform(get("/listas/1/tarjetas")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Retornar 201 CREATED al crear una tarjeta")
    void crear_RetornaCreated() throws Exception {
        String body = "{\"nombre\":\"T1\", \"contenido\":{\"emailUsuario\":\"prueba@um.es\"}}";
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(post("/tableros/1/listas/1/tarjetas")
               .header("Authorization", "Bearer token-Válido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al editar una tarjeta")
    void editar_RetornaNoContent() throws Exception {
        String body = "{\"cmd\":{}}";
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(put("/tableros/1/listas/1/tarjetas/1/editar")
               .header("Authorization", "Bearer token-Válido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al renombrar una tarjeta")
    void renombrar_RetornaNoContent() throws Exception {
        String body = "{\"nuevoNombre\":\"Nuevo\"}";
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(put("/tableros/1/listas/1/tarjetas/1/renombrar")
               .header("Authorization", "Bearer token-Válido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al mover una tarjeta")
    void mover_RetornaNoContent() throws Exception {
        String body = "{\"destino\":\"2\"}";
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(put("/tableros/1/listas/1/tarjetas/1/mover")
               .header("Authorization", "Bearer token-Válido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al completar un item del checklist")
    void completarItem_RetornaNoContent() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        			       .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(put("/tableros/1/listas/1/tarjetas/1/checklist/0/completar")
               .header("Authorization", "Bearer token-Válido"))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al etiquetar una tarjeta")
    void etiquetar_RetornaNoContent() throws Exception {
        String body = "{\"nombre\":\"Urgente\", \"color\":\"rojo\"}";
        
        when(servicioSesion.validarYRenovarToken(anyString()))	
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(post("/tableros/1/listas/1/tarjetas/1/etiqueta")
               .header("Authorization", "Bearer token-Válido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Retornar 204 NO CONTENT al eliminar etiqueta de una tarjeta")
    void eliminarEtiqueta_RetornaNoContent() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));

        mockMvc.perform(delete("/tableros/1/listas/1/tarjetas/1/etiqueta")
               .param("nombre", "Urgente")
               .param("color", "rojo")
               .header("Authorization", "Bearer token-Válido"))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Retornar 409 CONFLICT al fallar una acción de tarjeta")
    void operacion_RetornaConflict() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        doThrow(new IllegalArgumentException("Error de tarjeta"))
            			   .when(servicioTarjeta).eliminarTarjeta(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(delete("/tableros/1/listas/1/tarjetas/1")
               .header("Authorization", "Bearer token-Válido"))
               .andExpect(status().isConflict())
               .andExpect(content().string("Error de tarjeta"));
    }

}
	