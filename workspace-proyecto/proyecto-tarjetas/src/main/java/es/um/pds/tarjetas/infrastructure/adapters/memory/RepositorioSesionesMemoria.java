package es.um.pds.tarjetas.infrastructure.adapters.memory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioSesiones;

@Repository
@Primary
public class RepositorioSesionesMemoria implements RepositorioSesiones {

	// Clase estática para modelar sesión guardada
	private static class SesionGuardada {
		private final UsuarioId usuarioId;
		private Instant expiraEn;

		private SesionGuardada(UsuarioId usuarioId, Instant expiraEn) {
			this.usuarioId = usuarioId;
			this.expiraEn = expiraEn;
		}
	}

	private final Map<String, SesionGuardada> sesiones = new HashMap<>();

	@Override
	public void guardarToken(String token, UsuarioId usuarioId, Instant expiraEn) {
		sesiones.put(token, new SesionGuardada(usuarioId, expiraEn));
	}

	// Buscar al usuario por el token de su sesión actual guardada
	@Override
	public Optional<UsuarioId> buscarUsuarioPorTokenVigente(String token) {
		SesionGuardada sesion = sesiones.get(token);

		if (sesion == null) {
			return Optional.empty();
		}

		if (Instant.now().isAfter(sesion.expiraEn)) {
			sesiones.remove(token);
			return Optional.empty();
		}

		return Optional.of(sesion.usuarioId);
	}

	@Override
	public void extenderExpiracion(String token, Instant nuevaExpiracion) {
		SesionGuardada sesion = sesiones.get(token);
		if (sesion != null) {
			sesion.expiraEn = nuevaExpiracion;
		}
	}

	@Override
	public void invalidarToken(String token) {
		sesiones.remove(token);
	}
}