package es.um.pds.tarjetas.domain.ports.output;

import java.util.Optional;

import es.um.pds.tarjetas.domain.model.plantilla.id.PlantillaId;
import es.um.pds.tarjetas.domain.model.plantilla.model.Plantilla;

public interface RepositorioPlantillas {

	void guardar(Plantilla plantilla);

	Optional<Plantilla> buscarPorId(PlantillaId plantillaId);
}