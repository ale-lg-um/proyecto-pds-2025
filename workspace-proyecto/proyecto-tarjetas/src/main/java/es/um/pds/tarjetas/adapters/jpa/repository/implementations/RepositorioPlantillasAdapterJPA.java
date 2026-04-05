package es.um.pds.tarjetas.adapters.jpa.repository.implementations;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.mappers.PlantillaMapperJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.PlantillaRepositoryJPA;
import es.um.pds.tarjetas.domain.model.plantilla.id.PlantillaId;
import es.um.pds.tarjetas.domain.model.plantilla.model.Plantilla;
import es.um.pds.tarjetas.domain.ports.output.RepositorioPlantillas;

@Repository
@Primary
public class RepositorioPlantillasAdapterJPA implements RepositorioPlantillas {

	// Inyección de dependencias necesarias
	private final PlantillaRepositoryJPA plantillaRepositoryJPA;

	public RepositorioPlantillasAdapterJPA(PlantillaRepositoryJPA plantillaRepositoryJPA) {
		this.plantillaRepositoryJPA = plantillaRepositoryJPA;
	}

	/*
	 * Usa el método save de JpaRepository
	 */
	@Override
	public void guardar(Plantilla plantilla) {
		if (plantilla == null) {
			throw new IllegalArgumentException("La plantilla no puede ser nula");
		}
		plantillaRepositoryJPA.save(PlantillaMapperJPA.toEntity(plantilla));
	}

	/*
	 * Usa el método findById de JpaRepository
	 */
	@Override
	public Optional<Plantilla> buscarPorId(PlantillaId plantillaId) {
		if (plantillaId == null) {
			throw new IllegalArgumentException("El identificador de la plantilla no puede ser nulo");
		}
		return plantillaRepositoryJPA.findById(plantillaId.toString()).map(PlantillaMapperJPA::toDomain);
	}
}