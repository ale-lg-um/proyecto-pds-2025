package es.um.pds.tarjetas.infrastructure.adapters.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.domain.model.plantilla.id.PlantillaId;
import es.um.pds.tarjetas.domain.model.plantilla.model.Plantilla;
import es.um.pds.tarjetas.domain.ports.output.RepositorioPlantillas;

@Repository
public class RepositorioPlantillasMemoria implements RepositorioPlantillas {
	// Atribuos
	private final Map<PlantillaId, Plantilla> plantillas = new HashMap<>();
	
	@Override
	public void guardar(Plantilla plantilla) {
		plantillas.put(plantilla.getIdentificador(), plantilla);
	}
	
	@Override
	public Optional<Plantilla> buscarPorId(PlantillaId id) {
		return Optional.ofNullable(plantillas.get(id));
	}
}
