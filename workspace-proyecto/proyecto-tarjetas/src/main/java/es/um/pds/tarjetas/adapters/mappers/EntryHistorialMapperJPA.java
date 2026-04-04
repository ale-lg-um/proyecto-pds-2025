package es.um.pds.tarjetas.adapters.mappers;

import es.um.pds.tarjetas.adapters.jpa.entity.EntryHistorialEntity;
import es.um.pds.tarjetas.domain.model.entryHistorial.id.EntryHistorialId;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.EntryHistorial;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.TipoEntryHistorial;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

/**
 * Mapper entre EntryHistorial del dominio y EntryHistorialEntity de JPA
 */
public class EntryHistorialMapperJPA {

	// Constructor vacío, solo queremos los métodos, clase no instanciable
	private EntryHistorialMapperJPA() {
	}

	public static EntryHistorialEntity toEntity(EntryHistorial d) {
		return new EntryHistorialEntity(d.getIdentificador().toString(), d.getTableroId().toString(),
				d.getTipo().name(), d.getUsuario().toString(), d.getTimestamp(), d.getDetalles());
	}

	public static EntryHistorial toDomain(EntryHistorialEntity e) {
		return EntryHistorial.of(EntryHistorialId.of(e.getId()), TableroId.of(e.getTableroId()),
				TipoEntryHistorial.valueOf(e.getTipo()), UsuarioId.of(e.getEmailActor()), e.getTimestamp(),
				e.getDetalles());
	}
}