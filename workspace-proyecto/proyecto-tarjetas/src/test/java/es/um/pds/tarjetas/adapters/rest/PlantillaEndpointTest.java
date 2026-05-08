package es.um.pds.tarjetas.adapters.rest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import es.um.pds.tarjetas.domain.ports.input.dto.PlantillaDTO;
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

public class PlantillaEndpointTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ServicioSesion servicioSesion;
    
    @MockBean
    private ServicioPlantilla servicioPlantilla;
    
    @Test
    @DisplayName("Retornar 200 OK al obtener una plantilla por id")
    void obtenerPorId_RetornaOk() throws Exception {
        PlantillaDTO plantilla = new PlantillaDTO("1", "NombrePlantilla", "Contenido YAML");
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        when(servicioPlantilla.obtenerPlantilla("1"))
        					  .thenReturn(plantilla);

        mockMvc.perform(get("/plantillas/1")
               .header("Authorization", "Bearer token-Valido"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    @DisplayName("Retornar 409 CONFLICT al obtener una plantilla por id")
    void obtenerPorId_RetornaConflict() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        when(servicioPlantilla.obtenerPlantilla("1"))
        					  .thenThrow(new IllegalArgumentException("Plantilla no encontrada"));

        mockMvc.perform(get("/plantillas/1")
               .header("Authorization", "Bearer token-Valido"))
               .andExpect(status().isConflict())
               .andExpect(content().string("Plantilla no encontrada"));
    }

    @Test
    @DisplayName("Retornar 200 OK al obtener todas las plantillas")
    void obtenerTodas_RetornaOk() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        when(servicioPlantilla.listarPlantillas())
        					  .thenReturn(List.of());

        mockMvc.perform(get("/plantillas")
               .header("Authorization", "Bearer token-Valido"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Retornar 409 CONFLICT al obtener todas las plantillas")
    void obtenerTodas_RetornaConflict() throws Exception {
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenThrow(new IllegalArgumentException("Token inválido"));

        mockMvc.perform(get("/plantillas")
               .header("Authorization", "Bearer token-Inválido"))
               .andExpect(status().isConflict())
               .andExpect(content().string("Token inválido"));
    }

    @Test
    @DisplayName("Retornar 200 OK al crear una plantilla")
    void crear_RetornaOk() throws Exception {
        String body = "{\"yaml\":\"nombre: test\"}";
        PlantillaDTO plantilla = new PlantillaDTO("1", "NombrePlantilla", "nombre: test");
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        when(servicioPlantilla.crearPlantilla(anyString(), eq("prueba@um.es")))
        					  .thenReturn(plantilla);

        mockMvc.perform(post("/plantillas")
               .header("Authorization", "Bearer token-Valido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(body))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    @DisplayName("Retornar 409 CONFLICT al crear una plantilla")
    void crear_RetornaConflict() throws Exception {
        String body = "{\"yaml\":\"invalid\"}";
        
        when(servicioSesion.validarYRenovarToken(anyString()))
        				   .thenReturn(UsuarioId.of("prueba@um.es"));
        when(servicioPlantilla.crearPlantilla(anyString(), anyString()))
        					  .thenThrow(new IllegalArgumentException("YAML inválido"));

        mockMvc.perform(post("/plantillas")
                .header("Authorization", "Bearer token-Valido")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict())
                .andExpect(content().string("YAML inválido"));
    }
}
	