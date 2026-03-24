package es.um.pds.tarjetas.domain.ports.output;

import java.util.Optional;

import es.um.pds.tarjetas.domain.model.usuario.Usuario;
import es.um.pds.tarjetas.domain.model.usuario.UsuarioId;

public interface RepositorioUsuarios {

	void guardar(Usuario usuario);

	Optional<Usuario> buscarPorEmail(UsuarioId usuarioId);
}