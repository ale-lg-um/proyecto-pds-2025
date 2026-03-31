package es.um.pds.tarjetas.infrastructure.adapters;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import es.um.pds.tarjetas.application.common.EspecificacionTableroPlantilla;
import es.um.pds.tarjetas.application.common.EspecificacionTableroPlantilla.EspecificacionListaPlantilla;
import es.um.pds.tarjetas.domain.ports.output.PuertoParserYAML;

// Convertir un String YAML en un objeto Java
@Component
public class ParserYAMLImpl implements PuertoParserYAML {

	// Utilizamos SnakeYAML contenido en SpringBoot
	private final Yaml yamlParser = new Yaml();

	// Método auxiliar
	
	private EspecificacionListaPlantilla mapLista(Map<String, Object> raw) {

		// --- nombre ---
		String nombre = raw.get("nombre") != null ? raw.get("nombre").toString() : null;

		// --- limite ---
		Number limiteNum = raw.get("limite") instanceof Number n ? n : null;
		Integer limite = limiteNum != null ? limiteNum.intValue() : null;

		// --- prerrequisitos ---
		Object prerreqObj = raw.get("prerrequisitos");

		List<String> prerrequisitos;

		if (prerreqObj == null) {
			prerrequisitos = List.of();
		} else if (prerreqObj instanceof List<?> list) {
			prerrequisitos = list.stream().map(Object::toString).toList();
		} else {
			throw new RuntimeException("El campo 'prerrequisitos' debe ser una lista");
		}

		// --- especial ---
		Boolean especial = raw.get("especial") instanceof Boolean b ? b : false;

		return new EspecificacionListaPlantilla(nombre, limite, prerrequisitos, especial);
	}
	
	@Override
	public EspecificacionTableroPlantilla parse(String yaml) {

		try {
			// Primero se parsea el YAML a un objeto genérico
			Object loaded = yamlParser.load(yaml);

			/*
			 * Validar que el YAML tiene forma de objeto, que sea un Map,
			 * no una lista o valor simple. Algo como:
			 * nombre: ...
			 * listas: ...
			 */
			if (!(loaded instanceof Map<?, ?> data)) {
				throw new RuntimeException("El YAML no tiene una estructura válida");
			}

			// Leer el nombre de la plantilla
			Object nombreObj = data.get("nombre");
			String nombrePlantilla = nombreObj != null ? nombreObj.toString() : null;

			// Leer las listas
			Object listasObj = data.get("listas");

			/*
			 * Validación, asegura que esto sea realmente una lista:
			 * listas:
			 * 	- ...
			 */
			if (!(listasObj instanceof List<?> listasRaw)) {
				throw new RuntimeException("El campo 'listas' debe ser una lista");
			}

			// Convertir cada elemento de la lista (nombre, límite...)
			List<EspecificacionListaPlantilla> listas = listasRaw.stream().map(item -> {
				// Validar cada elemento
				if (!(item instanceof Map<?, ?> rawMap)) {
					throw new RuntimeException("Elemento de lista inválido en YAML");
				}

				@SuppressWarnings("unchecked")
				Map<String, Object> casted = (Map<String, Object>) rawMap;

				// Cada lista transformada en EspecificacionListaPlantilla
				return mapLista(casted);
			}).toList();

			return new EspecificacionTableroPlantilla(nombrePlantilla, listas);

		} catch (Exception e) {
			throw new RuntimeException("Error parseando YAML de plantilla", e);
		}
	}
}