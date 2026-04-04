package es.um.pds.tarjetas.adapters.jpa.repository.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.mappers.TableroMapperJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.TableroRepositoryJPA;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;

/*
 * Adaptador para JPA de la interfaz RepositorioTableros,
 * manteniendo los métodos originales de la interfaz
 * que también tienen una implementación en memoria
 */
@Repository
public class RepositorioTablerosAdapterJPA implements RepositorioTableros {

	// Inyección de dependencias necesarias
	private final TableroRepositoryJPA tableroRepositoryJPA;

	public RepositorioTablerosAdapterJPA(TableroRepositoryJPA tableroRepositoryJPA) {
		this.tableroRepositoryJPA = tableroRepositoryJPA;
	}

	/*
	 * Usa el método save de JpaRepository
	 */
	@Override
	public void guardar(Tablero tablero) {
		if (tablero == null) {
			throw new IllegalArgumentException("El tablero no puede ser nulo");
		}
		tableroRepositoryJPA.save(TableroMapperJPA.toEntity(tablero));
	}

	/*
	 * Usa el método findById de JpaRepository
	 */
	@Override
	public Optional<Tablero> buscarPorId(TableroId tableroId) {
		if (tableroId == null) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser nulo");
		}
		return tableroRepositoryJPA.findById(tableroId.toString()).map(TableroMapperJPA::toDomain);
	}

	/*
	 * Usa el método findByTokenUrl cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada
	 */
	@Override
	public Optional<Tablero> buscarPorURL(String tokenURL) {
		if (tokenURL == null || tokenURL.isBlank()) {
			throw new IllegalArgumentException("El token URL no puede ser nulo ni vacío");
		}
		return tableroRepositoryJPA.findByTokenUrl(tokenURL).map(TableroMapperJPA::toDomain);
	}

	/*
	 * Usa el método deleteById de JpaRepository
	 */
	@Override
	public void eliminarPorId(TableroId tableroId) {
		if (tableroId == null) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser nulo");
		}
		tableroRepositoryJPA.deleteById(tableroId.toString());
	}

	/*
	 * Usa el método findByCreadorId cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada
	 */
	@Override
	public List<TableroId> listarIdsPorUsuario(UsuarioId usuarioId) {
		if (usuarioId == null) {
			throw new IllegalArgumentException("El identificador del usuario no puede ser nulo");
		}
		return tableroRepositoryJPA.findByCreadorId(usuarioId.toString()).stream()
				.map(entity -> TableroId.of(entity.getId())).toList();
	}
}