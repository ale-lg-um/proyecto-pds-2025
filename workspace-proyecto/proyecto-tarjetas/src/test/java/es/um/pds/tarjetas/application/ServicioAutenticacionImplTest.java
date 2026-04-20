package es.um.pds.tarjetas.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioAutenticacion;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;
import es.um.pds.tarjetas.domain.ports.output.RepositorioCodigosLogin;
import es.um.pds.tarjetas.domain.ports.output.RepositorioUsuarios;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
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
	private JavaMailSender mailSender;

	@Test
	@DisplayName("Solicitar código crea el usuario, guarda el código y envía el correo")
	void enviarCodigoLogin_creaUsuarioYGestionaCodigo() {
		UsuarioId usuarioId = UsuarioId.of("login@um.es");

		servicioAutenticacion.enviarCodigoLogin(usuarioId.getCorreo());

		assertNotNull(repoUsuarios.buscarPorEmail(usuarioId).orElse(null));
		assertNotNull(repoCodigos.buscarCodigoVigente(usuarioId).orElse(null));
		verify(mailSender, atLeastOnce()).send(any(SimpleMailMessage.class));
	}

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
		// Verificar que se lanza una excepción si se reutiliza el mismo código
		assertThrows(IllegalArgumentException.class,
				() -> servicioAutenticacion.verificarCodigoLogin(usuarioId.getCorreo(), codigo));
	}
}
