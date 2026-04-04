package es.um.pds.tarjetas.infrastructure.adapters.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.model.usuario.model.Usuario;
import es.um.pds.tarjetas.domain.ports.output.RepositorioUsuarios;

@Repository
@Primary
public class RepositorioUsuariosMemoria implements RepositorioUsuarios {
	// Atributos
	private final Map<UsuarioId, Usuario> usuarios = new HashMap<>();
	
	@Override
	public void guardar(Usuario usuario) {
		usuarios.put(usuario.getIdentificador(), usuario);
	}
	
	@Override
	public Optional<Usuario> buscarPorEmail(UsuarioId email) {
		return Optional.ofNullable(usuarios.get(email));
	}
}
