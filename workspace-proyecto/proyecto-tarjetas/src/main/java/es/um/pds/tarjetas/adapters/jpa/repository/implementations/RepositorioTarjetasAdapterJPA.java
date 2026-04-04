package es.um.pds.tarjetas.adapters.jpa.repository.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.jpa.entity.TarjetaEntity;
import es.um.pds.tarjetas.adapters.mappers.TarjetaMapperJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.TarjetaRepositoryJPA;
import es.um.pds.tarjetas.application.common.Page;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.ports.output.ModoFiltradoEtiquetas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;

@Repository
public class RepositorioTarjetasAdapterJPA implements RepositorioTarjetas {

	// Inyección de dependencias necesarias
	private final TarjetaRepositoryJPA tarjetaRepositoryJPA;

	public RepositorioTarjetasAdapterJPA(TarjetaRepositoryJPA tarjetaRepositoryJPA) {
		this.tarjetaRepositoryJPA = tarjetaRepositoryJPA;
	}

	/*
	 * Usa el método save de JpaRepository
	 */
	@Override
	public void guardar(Tarjeta tarjeta) {
		if (tarjeta == null) {
			throw new IllegalArgumentException("La tarjeta no puede ser nula");
		}
		tarjetaRepositoryJPA.save(TarjetaMapperJPA.toEntity(tarjeta));
	}

	/*
	 * Usa el método findById de JpaRepository
	 */
	@Override
	public Optional<Tarjeta> buscarPorId(TarjetaId tarjetaId) {
		if (tarjetaId == null) {
			throw new IllegalArgumentException("El identificador de la tarjeta no puede ser nulo");
		}
		return tarjetaRepositoryJPA.findById(tarjetaId.toString()).map(TarjetaMapperJPA::toDomain);
	}

	/*
	 * Usa el método findByListaActualId cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada
	 */
	@Override
	public List<Tarjeta> buscarPorListaId(ListaId listaId) {
		if (listaId == null) {
			throw new IllegalArgumentException("El identificador de la lista no puede ser nulo");
		}
		return tarjetaRepositoryJPA.findByListaActualId(listaId.toString()).stream().map(TarjetaMapperJPA::toDomain)
				.toList();
	}

	/*
	 * Usa el método findByTableroId cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada
	 */
	@Override
	public List<Tarjeta> buscarPorTableroId(TableroId tableroId) {
		if (tableroId == null) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser nulo");
		}
		return tarjetaRepositoryJPA.findByTableroId(tableroId.toString()).stream().map(TarjetaMapperJPA::toDomain)
				.toList();
	}

	/*
	 * Usa el método deleteById de JpaRepository
	 */
	@Override
	public void eliminarPorId(TarjetaId tarjetaId) {
		if (tarjetaId == null) {
			throw new IllegalArgumentException("El identificador de la tarjeta no puede ser nulo");
		}
		tarjetaRepositoryJPA.deleteById(tarjetaId.toString());
	}

	/*
	 * Usa el método deleteByListaActualId cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada
	 */
	@Override
	public void eliminarPorListaId(ListaId listaId) {
		if (listaId == null) {
			throw new IllegalArgumentException("El identificador de la lista no puede ser nulo");
		}
		tarjetaRepositoryJPA.deleteByListaActualId(listaId.toString());
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
		tarjetaRepositoryJPA.deleteByTableroId(tableroId.toString());
	}

	/*
	 * Usa los métodos de filtrado de etiquetas cuya implementación está generada
	 * automáticamente con Spring Data JPA a partir de una Query manual
	 */
	@Override
	public Page<Tarjeta> filtrarPorEtiquetas(TableroId tableroId, List<String> nombresEtiquetas,
			ModoFiltradoEtiquetas modo, es.um.pds.tarjetas.application.common.PageRequest pageRequest) {

		if (tableroId == null) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser nulo");
		}
		if (nombresEtiquetas == null || nombresEtiquetas.isEmpty()) {
			throw new IllegalArgumentException("La lista de etiquetas no puede ser nula ni vacía");
		}
		if (modo == null) {
			throw new IllegalArgumentException("El modo de filtrado no puede ser nulo");
		}
		if (pageRequest == null) {
			throw new IllegalArgumentException("La petición de página no puede ser nula");
		}

		List<String> nombresNormalizados = nombresEtiquetas.stream().map(String::trim).toList();

		// Objeto necesario para paginación a la hora de filtrar etiquetas
		// Spring necesita un objeto de tipo Pageable a partir de nuestra implementación de Page
		// En resumen, adaptar nuestro modelo de paginación al de JPA
		PageRequest pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
		org.springframework.data.domain.Page<TarjetaEntity> resultado;

		if (modo == ModoFiltradoEtiquetas.AND) {
			resultado = tarjetaRepositoryJPA.filtrarPorEtiquetasAND(tableroId.toString(), nombresNormalizados,
					nombresNormalizados.size(), pageable);
		} else {
			resultado = tarjetaRepositoryJPA.filtrarPorEtiquetasOR(tableroId.toString(), nombresNormalizados, pageable);
		}

		// Obtener las tarjetas que cumplen con el filtro
		List<Tarjeta> contenido = resultado.getContent().stream().map(TarjetaMapperJPA::toDomain).toList();

		// Devolver las tarjetas en nuestro formato paginado
		return new Page<>(contenido, resultado.getNumber(), resultado.getSize(), resultado.getTotalPages(),
				(int) resultado.getTotalElements());
	}
}