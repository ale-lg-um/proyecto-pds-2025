package es.um.pds.tarjetas.infrastructure.adapters.memory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioCodigosLogin;

//@Repository
public class RepositorioCodigosLoginMemoria implements RepositorioCodigosLogin {

	// Clase estática para los códigos de login guardados
	private static class CodigoLoginGuardado {
		private final String codigo;
		private final Instant expiraEn;

		private CodigoLoginGuardado(String codigo, Instant expiraEn) {
			this.codigo = codigo;
			this.expiraEn = expiraEn;
		}
	}

	private final Map<UsuarioId, CodigoLoginGuardado> codigos = new HashMap<>();

	@Override
	public void guardarCodigo(UsuarioId usuarioId, String codigo, Instant expiraEn) {
		codigos.put(usuarioId, new CodigoLoginGuardado(codigo, expiraEn));
	}

	@Override
	public Optional<String> buscarCodigoVigente(UsuarioId usuarioId) {
		CodigoLoginGuardado guardado = codigos.get(usuarioId);

		if (guardado == null) {
			return Optional.empty();
		}

		if (Instant.now().isAfter(guardado.expiraEn)) {
			codigos.remove(usuarioId);
			return Optional.empty();
		}

		return Optional.of(guardado.codigo);
	}

	@Override
	public void invalidarCodigo(UsuarioId usuarioId) {
		codigos.remove(usuarioId);
	}
}