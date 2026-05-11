package es.um.pds.tarjetas.adapters.mappers;

import java.util.stream.Collectors;

import es.um.pds.tarjetas.adapters.jpa.embeddable.EstadoBloqueoEmbeddable;
import es.um.pds.tarjetas.adapters.jpa.entity.TableroEntity;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.EstadoBloqueo;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

/**
 * Mapper entre Tablero del dominio y TableroEntity de JPA
 */
public class TableroMapperJPA {

	// Constructor vacío, solo queremos los métodos, clase no instanciable
	private TableroMapperJPA() {
	}

	/*
	 * Versión anterior
	public static TableroEntity toEntity(Tablero d) {
		return new TableroEntity(d.getIdentificador().toString(), d.getNombre(), d.getTokenURL(),
				d.getCreador().toString(), d.getListaEspecial() != null ? d.getListaEspecial().toString() : null,
				d.getListas().stream().map(Object::toString).collect(Collectors.toSet()),
				toEmbeddable(d.getEstadoBloqueo()));
	}
	*/
	
	public static TableroEntity toEntity(Tablero d) {
		return new TableroEntity(d.getIdentificador().getId(), d.getNombre(), d.getTokenURL(),
				d.getCreador().getCorreo(), d.getListaEspecial() != null ? d.getListaEspecial().getId() : null,
				d.getListas().stream().map(ListaId::getId).collect(Collectors.toSet()),
				toEmbeddable(d.getEstadoBloqueo()));
	}

	public static Tablero toDomain(TableroEntity e) {
		return Tablero.reconstruir(TableroId.of(e.getId()), e.getNombre(), e.getTokenUrl(),
				UsuarioId.of(e.getCreadorId()), e.getListas().stream().map(ListaId::of).collect(Collectors.toSet()),
				e.getListaEspecialId() != null ? ListaId.of(e.getListaEspecialId()) : null,
				toDomainEstado(e.getEstadoBloqueo()));
	}

	private static EstadoBloqueoEmbeddable toEmbeddable(EstadoBloqueo e) {
		if (e == null)
			return null;
		return new EstadoBloqueoEmbeddable(e.getDesde(), e.getHasta(), e.getDescripcion());
	}

	// Método auxiliar
	private static EstadoBloqueo toDomainEstado(EstadoBloqueoEmbeddable e) {
		if (e == null)
			return null;
		return new EstadoBloqueo(e.getDesde(), e.getHasta(), e.getDescripcion());
	}
}