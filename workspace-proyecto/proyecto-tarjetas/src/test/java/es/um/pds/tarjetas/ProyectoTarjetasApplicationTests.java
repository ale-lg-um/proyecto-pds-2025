package es.um.pds.tarjetas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import es.um.pds.tarjetas.domain.ports.output.PuertoEnvioEmail;

//Levanta el contexto de la aplicación con test SpringBoot
@SpringBootTest

// Activa el perfil test de Spring, porque se pueden tener distintos entornos
@ActiveProfiles("test")

//Propiedades específicas para el test
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:tarjetasdb_test;DB_CLOSE_DELAY=-1",
		"spring.datasource.driverClassName=org.h2.Driver", "spring.datasource.username=sa",
		"spring.datasource.password=", "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false",
		"spring.mail.username=test@ejemplo.com" })
class ProyectoTarjetasApplicationTests {

	@MockBean
	PuertoEnvioEmail puertoEnvioEmail;
	
	@Test
	void contextLoads() {
	}

}
