package es.um.pds.tarjetas.domain.ports.output.plantilla;

import java.util.Optional;

import es.um.pds.tarjetas.domain.model.plantilla.Plantilla;
import es.um.pds.tarjetas.domain.model.plantilla.PlantillaId;

public interface RepositorioPlantillas {

	void guardar(Plantilla plantilla);

	Optional<Plantilla> buscarPorId(PlantillaId plantillaId);
}