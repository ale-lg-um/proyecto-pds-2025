package es.um.pds.tarjetas.application.usecases;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.application.common.EspecificacionTableroPlantilla;
import es.um.pds.tarjetas.application.common.exceptions.PlantillaInvalidaException;
import es.um.pds.tarjetas.common.events.EventBus;
import es.um.pds.tarjetas.domain.model.plantilla.eventos.PlantillaCreada;
import es.um.pds.tarjetas.domain.model.plantilla.id.PlantillaId;
import es.um.pds.tarjetas.domain.model.plantilla.model.Plantilla;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioPlantilla;
import es.um.pds.tarjetas.domain.ports.input.dto.PlantillaDTO;
import es.um.pds.tarjetas.domain.ports.output.PuertoParserYAML;
import es.um.pds.tarjetas.domain.ports.output.RepositorioPlantillas;

@Service
public class ServicioPlantillaImpl implements ServicioPlantilla {

	private final RepositorioPlantillas repoPlantillas;
	private final PuertoParserYAML parserYAML;
	private final EventBus eventBus;

	public ServicioPlantillaImpl(RepositorioPlantillas repoPlantillas, PuertoParserYAML parserYAML, EventBus eventBus) {
		this.repoPlantillas = repoPlantillas;
		this.parserYAML = parserYAML;
		this.eventBus = eventBus;
	}

	// Métodos auxiliares

	// La plantilla se crea a partir de un tablero que mantiene las reglas de
	// negocio, consistencia y se adhiere a historias de usuario, por lo que 
	// validamos que la plantilla haga lo mismo
	private void validarEspecificacion(EspecificacionTableroPlantilla especificacion) {
		if (especificacion == null) {
			throw new PlantillaInvalidaException("No se ha podido interpretar la plantilla YAML");
		}

		if (especificacion.getNombrePlantilla() == null || especificacion.getNombrePlantilla().isBlank()) {
			throw new PlantillaInvalidaException("La plantilla debe tener un nombre");
		}

		if (especificacion.getListas() == null || especificacion.getListas().isEmpty()) {
			throw new PlantillaInvalidaException("La plantilla debe contener al menos una lista");
		}

		long listasEspeciales = especificacion.getListas().stream()
				.filter(EspecificacionTableroPlantilla.EspecificacionListaPlantilla::isEspecial).count();

		if (listasEspeciales > 1) {
			throw new PlantillaInvalidaException("La plantilla no puede tener más de una lista especial");
		}

		especificacion.getListas().forEach(lista -> {
			if (lista.getNombre() == null || lista.getNombre().isBlank()) {
				throw new PlantillaInvalidaException("Todas las listas de la plantilla deben tener nombre");
			}

			if (lista.getLimite() != null && lista.getLimite() < 0) {
				throw new PlantillaInvalidaException(
						"El límite de una lista no puede ser negativo: " + lista.getNombre());
			}

			if (lista.getPrerrequisitos() != null) {
				boolean hayPrerrequisitoVacio = lista.getPrerrequisitos().stream()
						.anyMatch(nombre -> nombre == null || nombre.isBlank());

				if (hayPrerrequisitoVacio) {
					throw new PlantillaInvalidaException(
							"Los prerrequisitos de una lista no pueden contener nombres vacíos");
				}

				boolean autoreferencia = lista.getPrerrequisitos().stream()
						.anyMatch(nombre -> nombre.equals(lista.getNombre()));

				if (autoreferencia) {
					throw new PlantillaInvalidaException(
							"Una lista no puede tenerse a sí misma como prerrequisito: " + lista.getNombre());
				}
			}
		});
	}

	private PlantillaDTO toDTO(Plantilla plantilla) {
		return new PlantillaDTO(plantilla.getIdentificador().getId(), plantilla.getNombre(),
				plantilla.getContenidoYaml());
	}

	@Override
	@Transactional
	public PlantillaDTO crearPlantilla(String yaml, String emailUsuario) {

		// 1. Validaciones de frontera
		if (yaml == null || yaml.isBlank()) {
			throw new IllegalArgumentException("El contenido YAML de la plantilla no puede estar vacío");
		}

		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede ser nulo o vacío");
		}

		// 2. Construcción de objetos de dominio
		UsuarioId usuarioId = UsuarioId.of(emailUsuario);

		// 3. Parseo y validación semántica de la especificación
		// Tras parsear, deja en especificacion el contenido de la plantilla, se valida
		// y tras validarlo está listo para crear el objeto Plantilla
		EspecificacionTableroPlantilla especificacion = parserYAML.parse(yaml);

		validarEspecificacion(especificacion);

		// 4. Creación del agregado
		PlantillaId plantillaId = PlantillaId.of();
		Plantilla plantilla = Plantilla.of(plantillaId, especificacion.getNombrePlantilla(), yaml);

		// 5. Persistencia
		repoPlantillas.guardar(plantilla);

		// 6. Evento de dominio / aplicación
		eventBus.publicar(new PlantillaCreada(plantillaId, usuarioId, LocalDateTime.now(), plantilla.getNombre(),
				plantilla.getContenidoYaml()));

		// 7. Respuesta
		return toDTO(plantilla);
	}

	@Override
	@Transactional(readOnly = true)
	public PlantillaDTO obtenerYamlPlantilla(String plantillaId) {

		// 1. Validaciones de frontera
		if (plantillaId == null || plantillaId.isBlank()) {
			throw new IllegalArgumentException("El identificador de la plantilla no puede ser nulo o vacío");
		}

		// 2. Construcción de objetos de dominio
		PlantillaId id = PlantillaId.of(plantillaId);

		// 3. Recuperación
		Plantilla plantilla = repoPlantillas.buscarPorId(id)
				.orElseThrow(() -> new IllegalArgumentException("No existe la plantilla indicada"));

		// 4. Respuesta
		return toDTO(plantilla);
	}

}