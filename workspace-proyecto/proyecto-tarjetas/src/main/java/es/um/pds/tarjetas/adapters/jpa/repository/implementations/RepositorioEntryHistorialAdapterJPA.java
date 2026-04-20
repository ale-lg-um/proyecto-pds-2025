package es.um.pds.tarjetas.adapters.jpa.repository.implementations;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.adapters.jpa.entity.EntryHistorialEntity;
import es.um.pds.tarjetas.adapters.mappers.EntryHistorialMapperJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.EntryHistorialRepositoryJPA;
import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.EntryHistorial;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioEntryHistorial;

@Repository
@Primary
@Transactional
public class RepositorioEntryHistorialAdapterJPA implements RepositorioEntryHistorial {

	// Inyección de dependencias necesarias
	private final EntryHistorialRepositoryJPA entryHistorialRepositoryJPA;

	public RepositorioEntryHistorialAdapterJPA(EntryHistorialRepositoryJPA entryHistorialRepositoryJPA) {
		this.entryHistorialRepositoryJPA = entryHistorialRepositoryJPA;
	}

	/*
	 * Usa el método save de JpaRepository
	 */
	@Override
	public void guardar(EntryHistorial entry) {
		if (entry == null) {
			throw new IllegalArgumentException("La entrada del historial no puede ser nula");
		}
		entryHistorialRepositoryJPA.save(EntryHistorialMapperJPA.toEntity(entry));
	}

	/*
	 * Usa el método findByTableroIdOrderByTimestampDesc cuya implementación 
	 * genera automáticamente Spring Data JPA porque coincide con atributos reales
	 * de la entidad y sigue la estructura de nombres adecuada
	 */
	@Override
	public Page<EntryHistorial> consultarPorTablero(TableroId tableroId,
			es.um.pds.tarjetas.application.common.PageRequest pageRequest) {

		if (tableroId == null) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser nulo");
		}
		if (pageRequest == null) {
			throw new IllegalArgumentException("La petición de página no puede ser nula");
		}

		// Objeto necesario para paginación a la hora de mostrar entries del historial
		// Spring necesita un objeto de tipo Pageable a partir de nuestra implementación de Page
		// En resumen, adaptar nuestro modelo de paginación al de JPA
		PageRequest pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
		org.springframework.data.domain.Page<EntryHistorialEntity> resultado = entryHistorialRepositoryJPA
				.findByTableroIdOrderByTimestampDesc(tableroId.getId(), pageable);

		List<EntryHistorial> contenido = resultado.getContent().stream().map(EntryHistorialMapperJPA::toDomain)
				.toList();

		return new Page<>(contenido, resultado.getNumber(), resultado.getSize(), resultado.getTotalPages(),
				(int) resultado.getTotalElements());
	}

	/*
	 * Usa el método deleteByTableroId cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada
	 */
	@Override
	public void eliminarPorTableroId(TableroId tableroId) {
		if (tableroId == null) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser nulo");
		}
		entryHistorialRepositoryJPA.deleteByTableroId(tableroId.getId());
	}
}