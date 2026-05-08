package es.um.pds.tarjetas.adapters.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

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

public class ListaEndpointTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ServicioSesion servicioSesion;
    
    @Test
    @DisplayName("Retornar 409 CONFLICT cuando el token es inválido o ha caducado")
    void realizarPeticion_ConTokenInvalido_RetornaError() throws Exception {
        String tokenMalo = "token-caducado-o-falso";
        
        mockMvc.perform(get("/tableros/tablero-1/listas")
        			   .header("Authorization", "Bearer " + tokenMalo))
	            	   .andExpect(status().isConflict())
	            	   .andExpect(content().string("Token inválido o expirado"));
    }
}
	