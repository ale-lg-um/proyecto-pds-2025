package es.um.pds.tarjetas.domain.ports.output;

import java.time.Instant;
import java.util.Optional;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public interface RepositorioSesiones {

	void guardarToken(String token, UsuarioId usuarioId, Instant expiraEn);

	Optional<UsuarioId> buscarUsuarioPorTokenVigente(String token);

	void extenderExpiracion(String token, Instant nuevaExpiracion);

	void invalidarToken(String token);
}