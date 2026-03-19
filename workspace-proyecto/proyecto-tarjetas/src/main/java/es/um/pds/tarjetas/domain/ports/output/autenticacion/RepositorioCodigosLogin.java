package es.um.pds.tarjetas.domain.ports.output.autenticacion;

import java.time.Instant;
import java.util.Optional;

import es.um.pds.tarjetas.domain.model.usuario.UsuarioId;

public interface RepositorioCodigosLogin {

	void guardarCodigo(UsuarioId usuarioId, String codigo, Instant expiraEn);

	Optional<String> buscarCodigoVigente(UsuarioId usuarioId);

	void invalidarCodigo(UsuarioId usuarioId);
}