package es.um.pds.tarjetas.adapters.mappers;

import java.util.stream.Collectors;

import es.um.pds.tarjetas.adapters.jpa.entity.ListaEntity;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;

/**
 * Mapper entre Lista del dominio y ListaEntity de JPA
 */
public class ListaMapperJPA {

	// Constructor vacío, solo queremos los métodos, clase no instanciable
	private ListaMapperJPA() {
	}

	/*
	 * Versión anterior
	public static ListaEntity toEntity(Lista d) {
		return new ListaEntity(d.getIdentificador().toString(), d.getNombreLista(), d.isEspecial(), d.getLimite(),
				d.getTablero() != null ? d.getTablero().toString() : null,
				d.getListaTarjetas().stream().map(Object::toString).toList(),
				d.getPrerrequisitos().stream().map(Object::toString).collect(Collectors.toSet()));
	}
	*/
	public ListaEntity toEntity(Lista d) {
		if (d == null) {
			return null;
		}

		return new ListaEntity(d.getIdentificador().getId(), d.getNombreLista(), d.isEspecial(), d.getLimite(),
				d.getTablero() != null ? d.getTablero().getId() : null,
				d.getListaTarjetas().stream().map(t -> t.getId()).toList(),
				d.getPrerrequisitos().stream().map(l -> l.getId()).collect(Collectors.toSet()));
	}


	public static Lista toDomain(ListaEntity e) {
		return Lista.reconstruir(ListaId.of(e.getId()), e.getNombreLista(),
				e.getListaTarjetas().stream().map(TarjetaId::of).toList(), e.isEspecial(), e.getLimite(),
				e.getPrerrequisitos().stream().map(ListaId::of).collect(Collectors.toSet()),
				e.getTableroId() != null ? TableroId.of(e.getTableroId()) : null);
	}
}