package es.um.pds.tarjetas.application.usecases;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.model.usuario.models.Usuario;
import es.um.pds.tarjetas.domain.ports.input.ServicioAutenticacion;
import es.um.pds.tarjetas.domain.ports.output.PuertoEnvioEmail;
import es.um.pds.tarjetas.domain.ports.output.RepositorioCodigosLogin;
import es.um.pds.tarjetas.domain.ports.output.RepositorioSesiones;
import es.um.pds.tarjetas.domain.ports.output.RepositorioUsuarios;

@Service
public class ServicioAutenticacionImpl implements ServicioAutenticacion {

	// Constantes
	private static final long MINUTOS_VALIDEZ = 5;
	private static final SecureRandom RANDOM = new SecureRandom();

	// Inyectamos dependencias estrictas (patrón fachada)
	private final RepositorioUsuarios repoUsuarios;
	private final RepositorioCodigosLogin repoCodigos;
	private final RepositorioSesiones repoSesiones;
	private final PuertoEnvioEmail puertoEnvioEmail;

	// Constructor
	public ServicioAutenticacionImpl(RepositorioUsuarios repoUsuarios, RepositorioCodigosLogin repoCodigos,
			RepositorioSesiones repoSesiones, PuertoEnvioEmail puertoEnvioEmail) {
		this.repoUsuarios = repoUsuarios;
		this.repoCodigos = repoCodigos;
		this.repoSesiones = repoSesiones;
		this.puertoEnvioEmail = puertoEnvioEmail;
	}

	// Métodos auxiliares
	
	// Comprueba si el usuario existe, y si no, lo crea
	private void asegurarUsuarioExiste(UsuarioId usuarioId) {
		repoUsuarios.buscarPorEmail(usuarioId).orElseGet(() -> {
			Usuario nuevo = Usuario.of(usuarioId, usuarioId.getCorreo());
			repoUsuarios.guardar(nuevo);
			return nuevo;	// Necesario para usar la lambda-expresión
		});
	}

	private String generarCodigo6Digitos() {
		int numero = RANDOM.nextInt(900_000) + 100_000;
		return String.valueOf(numero);
	}

	private String generarTokenSesion() {
		return UUID.randomUUID().toString();
	}
	
	// Métodos heredados
	
	@Override
	@Transactional
	public void enviarCodigoLogin(String email) {
		
		// 1. Validación y construcción de objetos del dominio
		UsuarioId usuarioId = UsuarioId.of(email);

		// 2. Comprobación de que existe el usuario en el repositorio, si no, lo crea
		asegurarUsuarioExiste(usuarioId);

		// 3. Generación del código
		String codigo = generarCodigo6Digitos();
		Instant expiraEn = Instant.now().plus(MINUTOS_VALIDEZ, ChronoUnit.MINUTES);

		// 4. Guardar el código en el repositorio
		repoCodigos.guardarCodigo(usuarioId, codigo, expiraEn);

		// 5. Preparar el contenido del correo
		String asunto = "Tu código de acceso";
		String cuerpo = "Tu código de acceso es: " + codigo + "\nCaduca en " + MINUTOS_VALIDEZ + " minutos.";

		// 6. Enviar el email al usuario
		puertoEnvioEmail.enviarEmail(usuarioId, asunto, cuerpo);
	}

	@Override
	@Transactional
	public String verificarCodigoLogin(String email, String codigo) {
		if (codigo == null || codigo.isBlank()) {
			throw new IllegalArgumentException("El código no puede ser nulo o vacío");
		}

		UsuarioId usuarioId = UsuarioId.of(email);

		String codigoGuardado = repoCodigos.buscarCodigoVigente(usuarioId)
				.orElseThrow(() -> new IllegalArgumentException("No existe un código vigente para ese usuario"));

		if (!codigoGuardado.equals(codigo.trim())) {
			throw new IllegalArgumentException("El código introducido no es correcto");
		}

		repoCodigos.invalidarCodigo(usuarioId);

		String token = generarTokenSesion();
		Instant expiraEn = Instant.now().plus(MINUTOS_VALIDEZ, ChronoUnit.MINUTES);

		repoSesiones.guardarToken(token, usuarioId, expiraEn);

		return token;
	}
}