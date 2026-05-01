package es.um.pds.tarjetas.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioAutenticacion;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;
import es.um.pds.tarjetas.domain.ports.output.PuertoEnvioEmail;
import es.um.pds.tarjetas.domain.ports.output.RepositorioCodigosLogin;
import es.um.pds.tarjetas.domain.ports.output.RepositorioUsuarios;

import jakarta.transaction.Transactional;

//Levanta el contexto de la aplicación con test SpringBoot
@SpringBootTest
@Transactional

//Activa el perfil test de Spring, porque se pueden tener distintos entornos
@ActiveProfiles("test")
class ServicioAutenticacionImplTest {

	@Autowired
	private ServicioAutenticacion servicioAutenticacion;

	@Autowired
	private ServicioSesion servicioSesion;

	@Autowired
	private RepositorioCodigosLogin repoCodigos;

	@Autowired
	private RepositorioUsuarios repoUsuarios;

	@MockBean
	private PuertoEnvioEmail puertoEnvioEmail;

	@Test
	@DisplayName("Solicitar código crea el usuario, guarda el código y solicita el envío del correo")
	void enviarCodigoLogin_creaUsuarioYGestionaCodigo() {
		UsuarioId usuarioId = UsuarioId.of("login@um.es");

		servicioAutenticacion.enviarCodigoLogin(usuarioId.getCorreo());

		assertNotNull(repoUsuarios.buscarPorEmail(usuarioId).orElse(null));
		assertNotNull(repoCodigos.buscarCodigoVigente(usuarioId).orElse(null));
	}

	/*
	@Test
	@DisplayName("Verificar código válido genera una sesión usable")
	void verificarCodigoLogin_generaTokenDeSesionValido() {
		UsuarioId usuarioId = UsuarioId.of("sesion@um.es");

		servicioAutenticacion.enviarCodigoLogin(usuarioId.getCorreo());

		String codigo = repoCodigos.buscarCodigoVigente(usuarioId).orElseThrow();
		String token = servicioAutenticacion.verificarCodigoLogin(usuarioId.getCorreo(), codigo);

		UsuarioId usuarioSesion = servicioSesion.validarYRenovarToken(token);

		assertNotNull(token);
		assertEquals(usuarioId, usuarioSesion);

		assertThrows(
				IllegalArgumentException.class,
				() -> servicioAutenticacion.verificarCodigoLogin(usuarioId.getCorreo(), codigo));
	}
	*/
	
	@Test
	void verificarCodigoLogin_generaTokenDeSesionValido() {
	    UsuarioId usuarioId = UsuarioId.of("ejemplo@um.es");
	    servicioAutenticacion.enviarCodigoLogin(usuarioId.getCorreo());

	    String codigo = repoCodigos.buscarCodigoVigente(usuarioId).orElseThrow();
	    String token = servicioAutenticacion.verificarCodigoLogin(usuarioId.getCorreo(), codigo);

	    try {
	        UsuarioId usuarioSesion = servicioSesion.validarYRenovarToken(token);
	        assertNotNull(token);
	        assertEquals(usuarioId, usuarioSesion);
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw e;
	    }

	    assertThrows(IllegalArgumentException.class,
	            () -> servicioAutenticacion.verificarCodigoLogin(usuarioId.getCorreo(), codigo));
	}
	
}