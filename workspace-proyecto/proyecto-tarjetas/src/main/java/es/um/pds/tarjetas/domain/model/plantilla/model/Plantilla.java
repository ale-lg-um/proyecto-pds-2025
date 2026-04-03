package es.um.pds.tarjetas.domain.model.plantilla.model;

import java.util.Objects;

import es.um.pds.tarjetas.application.common.exceptions.PlantillaInvalidaException;
import es.um.pds.tarjetas.domain.model.plantilla.id.PlantillaId;

public class Plantilla {
	// Atributos
	private final PlantillaId identificador;
	private String nombre;
	private final String contenidoYaml;

	// Constructor
	private Plantilla(PlantillaId identificador, String nombre, String contenidoYaml) {
		this.identificador = identificador;
		this.nombre = nombre;
		// Se parsea y se valida el fichero en el servicio de aplicación
		// ServicioPlantilla
		this.contenidoYaml = contenidoYaml;
	}

	// Método factoría y reconstrucción
	public static Plantilla of(PlantillaId identificador, String nombre, String contenidoYaml) {
		if (identificador == null) {
			throw new PlantillaInvalidaException("La plantilla debe tener un identificador");
		}

		if (nombre == null || nombre.isBlank()) {
			throw new PlantillaInvalidaException("La plantilla debe tener un nombre");
		}

		if (contenidoYaml == null || contenidoYaml.isBlank()) {
			throw new PlantillaInvalidaException("La plantilla debe contener un YAML válido");
		}

		return new Plantilla(identificador, nombre, contenidoYaml);
	}

	// Getters
	public PlantillaId getIdentificador() {
		return this.identificador;
	}

	public String getNombre() {
		return this.nombre;
	}

	public String getContenidoYaml() {
		return this.contenidoYaml;
	}

	// Funcionalidades
	public void renombrar(String nuevoNombre) throws PlantillaInvalidaException {
		if (nuevoNombre == null || nuevoNombre.isBlank()) {
			throw new PlantillaInvalidaException("El nombre de la plantilla no puede estar vacío");
		}
		this.nombre = nuevoNombre;
	}

	// Overrides
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof Plantilla)) {
			return false;
		}
		Plantilla other = (Plantilla) obj;
		return Objects.equals(this.identificador, other.identificador);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identificador);
	}
}