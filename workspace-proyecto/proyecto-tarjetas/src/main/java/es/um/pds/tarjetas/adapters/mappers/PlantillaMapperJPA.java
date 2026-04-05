package es.um.pds.tarjetas.adapters.mappers;

import es.um.pds.tarjetas.adapters.jpa.entity.PlantillaEntity;
import es.um.pds.tarjetas.domain.model.plantilla.id.PlantillaId;
import es.um.pds.tarjetas.domain.model.plantilla.model.Plantilla;

/**
 * Mapper entre Plantilla del dominio y PlantillaEntity de JPA.
 */
public class PlantillaMapperJPA {

	// Constructor vacío, solo queremos los métodos, clase no instanciable
	private PlantillaMapperJPA() {
	}

	public static PlantillaEntity toEntity(Plantilla d) {
		return new PlantillaEntity(d.getIdentificador().getId(), d.getNombre(), d.getContenidoYaml());
	}

	public static Plantilla toDomain(PlantillaEntity e) {
		return Plantilla.of(PlantillaId.of(e.getId()), e.getNombre(), e.getContenidoYaml());
	}
}