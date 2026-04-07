package es.um.pds.tarjetas.adapters.jpa.repository.implementations;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.adapters.mappers.ListaMapperJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.ListaRepositoryJPA;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;

@Repository
@Primary
@Transactional
public class RepositorioListasAdapterJPA implements RepositorioListas {

	private final ListaRepositoryJPA listaRepositoryJPA;

	// Inyección de dependencias necesarias
	public RepositorioListasAdapterJPA(ListaRepositoryJPA listaRepositoryJPA) {
		this.listaRepositoryJPA = listaRepositoryJPA;
	}

	/*
	 * Usa el método save de JpaRepository
	 */
	@Override
	public void guardar(Lista lista) {
		if (lista == null) {
			throw new IllegalArgumentException("La lista no puede ser nula");
		}
		listaRepositoryJPA.save(ListaMapperJPA.toEntity(lista));
	}

	/*
	 * Usa el método findById de JpaRepository
	 */
	@Override
	public Optional<Lista> buscarPorId(ListaId listaId) {
		if (listaId == null) {
			throw new IllegalArgumentException("El identificador de la lista no puede ser nulo");
		}
		return listaRepositoryJPA.findById(listaId.getId()).map(ListaMapperJPA::toDomain);
	}

	/*
	 * Usa el método findByIdIn cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada
	 */
	@Override
	public Set<Lista> buscarPorIds(Set<ListaId> ids) {
		if (ids == null) {
			throw new IllegalArgumentException("El conjunto de identificadores no puede ser nulo");
		}
		// Convertir de Set<ListaId> a Set<String>
		Set<String> idsString = ids.stream().map(id -> {
			if (id == null) {
				throw new IllegalArgumentException("No puede haber identificadores nulos en el conjunto");
			}
			return id.toString();
		}).collect(Collectors.toSet());

		return listaRepositoryJPA.findByIdIn(idsString).stream().map(ListaMapperJPA::toDomain)
				.collect(Collectors.toSet());
	}

	/*
	 * Usa el método findByTableroId cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada
	 */
	@Override
	public Set<Lista> buscarPorTableroId(TableroId tableroId) {
		if (tableroId == null) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser nulo");
		}
		return listaRepositoryJPA.findByTableroId(tableroId.toString()).stream().map(ListaMapperJPA::toDomain)
				.collect(Collectors.toSet());
	}

	/*
	 * Usa el método deleteById de JpaRepository
	 */
	@Override
	public void eliminarPorId(ListaId listaId) {
		if (listaId == null) {
			throw new IllegalArgumentException("El identificador de la lista no puede ser nulo");
		}
		listaRepositoryJPA.deleteById(listaId.toString());
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
		listaRepositoryJPA.deleteByTableroId(tableroId.toString());
	}
}