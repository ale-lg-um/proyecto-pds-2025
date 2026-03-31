package es.um.pds.tarjetas.infrastructure.adapters;

import java.util.List;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import es.um.pds.tarjetas.application.common.EspecificacionTableroPlantilla;
import es.um.pds.tarjetas.application.common.EspecificacionTableroPlantilla.EspecificacionListaPlantilla;
import es.um.pds.tarjetas.application.common.EspecificacionTableroPlantilla.EspecificacionTarjetaPlantilla;
import es.um.pds.tarjetas.domain.model.tarjeta.model.TipoContenidoTarjeta;
import es.um.pds.tarjetas.domain.ports.output.PuertoParserYAML;

// Adaptador de infraestructura encargado de transformar un String YAML
// en una especificación Java que luego utilizará la capa de aplicación
// Aquí NO se aplican reglas de negocio complejas: solo se traduce la estructura
@Component
public class ParserYAMLImpl implements PuertoParserYAML {

	// Utilizamos SnakeYAML para parsear el texto YAML, disponible en SpringBoot
	// SnakeYAML transforma el YAML en estructuras Java genéricas:
	// - Map para objetos
	// - List para listas
	// - String / Number / Boolean para valores simples
	private final Yaml yamlParser = new Yaml();

	// Método auxiliar para transformar UNA lista del YAML en una EspecificacionListaPlantilla
	private EspecificacionListaPlantilla mapLista(java.util.Map<?, ?> raw) {

		/* NOMBRE:
		 * Obtenemos el nombre de la lista. Si no existe, dejamos null
		 * y la validación semántica se hará después
		 */
		String nombre = raw.get("nombre") != null ? raw.get("nombre").toString() : null;

		/* LÍMITE:
		 * SnakeYAML puede devolver Integer, Long, etc. Por eso usamos
		 * Number y luego convertimos con intValue()
		 */
		Number limiteNum = raw.get("limite") instanceof Number n ? n : null;
		Integer limite = limiteNum != null ? limiteNum.intValue() : null;

		/* PRERREQUISITOS:
		 * Esperamos una lista de nombres de listas. Si no aparece el campo,
		 * usamos lista vacía
		 */
		Object prerreqObj = raw.get("prerrequisitos");
		List<String> prerrequisitos;

		if (prerreqObj == null) {
			prerrequisitos = List.of();
		} else if (prerreqObj instanceof List<?> list) {
			// Convertimos todos los elementos a String
			prerrequisitos = list.stream()
					.map(Object::toString)
					.toList();
		} else {
			// Si el tipo no es correcto, el YAML tiene una forma inválida
			throw new RuntimeException("El campo 'prerrequisitos' debe ser una lista");
		}

		/* ESPECIAL:
		 * Si el campo que marca una lista como especial no está presente,
		 * asumimos que no lo es por defecto
		 */
		Boolean especial = raw.get("especial") instanceof Boolean b ? b : false;

		/* TARJETAS:
		 * Cada lista puede incluir tarjetas predeterminadas
		 * Si no aparece el campo, consideramos que la lista no trae tarjetas
		 */
		Object tarjetasObj = raw.get("tarjetas");
		List<EspecificacionTarjetaPlantilla> tarjetas;

		if (tarjetasObj == null) {
			tarjetas = List.of();
		} else if (tarjetasObj instanceof List<?> list) {
			// Transformamos cada elemento de la lista en una tarjeta de plantilla
			tarjetas = list.stream()
					.map(item -> {
						if (!(item instanceof java.util.Map<?, ?> rawMap)) {
							throw new RuntimeException("Elemento de tarjeta inválido en YAML");
						}
						return mapTarjeta(rawMap);
					})
					.toList();
		} else {
			throw new RuntimeException("El campo 'tarjetas' debe ser una lista");
		}

		// Finalmente creamos el objeto de especificación de lista
		return new EspecificacionListaPlantilla(
				nombre,
				limite,
				prerrequisitos,
				especial,
				tarjetas);
	}

	// Método auxiliar para transformar UNA tarjeta del YAML
	// en una EspecificacionTarjetaPlantilla
	private EspecificacionTarjetaPlantilla mapTarjeta(java.util.Map<?, ?> raw) {

		// --- titulo ---
		// Título visible de la tarjeta
		String titulo = raw.get("titulo") != null ? raw.get("titulo").toString() : null;

		// --- tipo ---
		// El YAML debe indicar si la tarjeta es de tipo TAREA o CHECKLIST
		// Convertimos el texto a enum
		TipoContenidoTarjeta tipoContenido = parseTipoContenido(raw.get("tipo"));

		// --- descripcion de tarea ---
		// Permitimos leer tanto "descripcionTarea" como "descripcion" por comodidad en el YAML
		Object descripcionObj = raw.containsKey("descripcionTarea")
				? raw.get("descripcionTarea")
				: raw.get("descripcion");

		String descripcionTarea = descripcionObj != null ? descripcionObj.toString() : null;

		// --- items de checklist ---
		// Solo tendrá sentido si la tarjeta es de tipo CHECKLIST,
		// pero aquí simplemente parseamos la estructura.
		Object itemsObj = raw.get("itemsChecklist");
		List<String> itemsChecklist;

		if (itemsObj == null) {
			itemsChecklist = List.of();
		} else if (itemsObj instanceof List<?> list) {
			itemsChecklist = list.stream()
					.map(Object::toString)
					.toList();
		} else {
			throw new RuntimeException("El campo 'itemsChecklist' debe ser una lista");
		}

		// Creamos la especificación de tarjeta
		return new EspecificacionTarjetaPlantilla(
				titulo,
				tipoContenido,
				descripcionTarea,
				itemsChecklist);
	}

	// Método auxiliar para convertir el valor textual del YAML
	// al enum TipoContenidoTarjeta
	private TipoContenidoTarjeta parseTipoContenido(Object tipoObj) {

		// Si no viene tipo, devolvemos null
		// La validación de negocio posterior decidirá si eso es aceptable o no
		if (tipoObj == null) {
			return null;
		}

		// Normalizamos el texto a mayúsculas
		String tipo = tipoObj.toString().trim().toUpperCase();

		try {
			return TipoContenidoTarjeta.valueOf(tipo);
		} catch (IllegalArgumentException e) {
			// Si el texto no coincide con ningún valor del enum,
			// lanzamos error indicando que el YAML es inválido
			throw new RuntimeException("Tipo de contenido de tarjeta no válido: " + tipo);
		}
	}

	@Override
	public EspecificacionTableroPlantilla parse(String yaml) {

		try {
			// Primero parseamos el YAML a un objeto Java genérico, normalmente un Map
			Object loaded = yamlParser.load(yaml);

			/*
			 * Validamos que el YAML raíz tenga forma de objeto:
			 *
			 * nombre: ...
			 * listas: ...
			 *
			 * Es decir, esperamos un Map y no una lista suelta o un valor simple
			 */
			if (!(loaded instanceof java.util.Map<?, ?> data)) {
				throw new RuntimeException("El YAML no tiene una estructura válida");
			}

			// --- nombre de la plantilla ---
			// Leemos el campo "nombre" de la plantilla
			Object nombreObj = data.get("nombre");
			String nombrePlantilla = nombreObj != null ? nombreObj.toString() : null;

			// --- listas ---
			// Leemos el campo "listas" de la plantilla
			Object listasObj = data.get("listas");

			/*
			 * Validamos que "listas" sea realmente una lista:
			 *
			 * listas:
			 *   - nombre: PENDIENTES
			 *   - nombre: COMPLETADAS
			 */
			if (!(listasObj instanceof List<?> listasRaw)) {
				throw new RuntimeException("El campo 'listas' debe ser una lista");
			}

			// Convertimos cada elemento del YAML en una EspecificacionListaPlantilla.
			List<EspecificacionListaPlantilla> listas = listasRaw.stream()
					.map(item -> {
						// Cada elemento de "listas" debe ser un objeto/mapa
						if (!(item instanceof java.util.Map<?, ?> rawMap)) {
							throw new RuntimeException("Elemento de lista inválido en YAML");
						}

						// Transformamos ese mapa en un objeto más expresivo
						return mapLista(rawMap);
					})
					.toList();

			// Finalmente construimos la especificación completa de la plantilla
			return new EspecificacionTableroPlantilla(nombrePlantilla, listas);

		} catch (Exception e) {
			// Envolvemos cualquier error para indicar claramente que el problema
			// ha sucedido durante el parseo del YAML de la plantilla
			throw new RuntimeException("Error parseando YAML de plantilla", e);
		}
	}
}