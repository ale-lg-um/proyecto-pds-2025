package es.um.pds.tarjetas.application.usecases;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioSesion;
import es.um.pds.tarjetas.domain.ports.output.RepositorioSesiones;

// Aunque se podría incluir dentro de ServicioAutenticacion, lo separamos para diferenciar mejor la semántica en el DDD
@Service
public class ServicioSesionImpl implements ServicioSesion {

	private static final long MINUTOS_VALIDEZ = 5;

	// Inyectamos dependencias necesarias (patrón fachada)
	private final RepositorioSesiones repoSesiones;

	public ServicioSesionImpl(RepositorioSesiones repoSesiones) {
		this.repoSesiones = repoSesiones;
	}

	@Transactional
	@Override
	public UsuarioId validarYRenovarToken(String token) {
		// 1. Validaciones de frontera
		if (token == null || token.isBlank()) {
			throw new IllegalArgumentException("Token ausente");
		}

		// 2. Extraer el ID de usuario por el token vigente si es válido y no ha expirado
		UsuarioId usuarioId = repoSesiones.buscarUsuarioPorTokenVigente(token)
				.orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));

		// Se extiende la expiración si se ha validado correctamente
		Instant nuevaExpiracion = Instant.now().plus(MINUTOS_VALIDEZ, ChronoUnit.MINUTES);
		repoSesiones.extenderExpiracion(token, nuevaExpiracion);

		return usuarioId;
	}
}