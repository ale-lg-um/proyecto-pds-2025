package es.um.pds.tarjetas.adapters.jpa.repository.implementations;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.adapters.mappers.UsuarioMapperJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.UsuarioRepositoryJPA;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.model.usuario.model.Usuario;
import es.um.pds.tarjetas.domain.ports.output.RepositorioUsuarios;

@Repository
@Primary
@Transactional
public class RepositorioUsuariosAdapterJPA implements RepositorioUsuarios {

	// Inyección de dependencias necesarias
	private final UsuarioRepositoryJPA usuarioRepositoryJPA;

	public RepositorioUsuariosAdapterJPA(UsuarioRepositoryJPA usuarioRepositoryJPA) {
		this.usuarioRepositoryJPA = usuarioRepositoryJPA;
	}

	/*
	 * Usa el método save de JpaRepository
	 */
	@Override
	public void guardar(Usuario usuario) {
		if (usuario == null) {
			throw new IllegalArgumentException("El usuario no puede ser nulo");
		}
		usuarioRepositoryJPA.save(UsuarioMapperJPA.toEntity(usuario));
	}

	/*
	 * Usa el método findById de JpaRepository
	 */
	@Override
	public Optional<Usuario> buscarPorEmail(UsuarioId usuarioId) {
		if (usuarioId == null) {
			throw new IllegalArgumentException("El identificador del usuario no puede ser nulo");
		}
		return usuarioRepositoryJPA.findById(usuarioId.getCorreo())
				.map(UsuarioMapperJPA::toDomain);
	}
}