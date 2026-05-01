package es.um.pds.tarjetas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import es.um.pds.tarjetas.domain.ports.output.PuertoEnvioEmail;

@SpringBootTest
class ProyectoTarjetasApplicationTests {

	// Necesario para que funcione la GitHub action
	@MockBean
	private PuertoEnvioEmail puertoEnvioEmail;
	
	@Test
	void contextLoads() {
	}

}
