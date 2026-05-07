package es.um.pds.tarjetas.domain.model.plantilla;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import es.um.pds.tarjetas.domain.model.tarjeta.model.TipoContenidoTarjeta;

public class EspecificacionTableroPlantilla {

	// EspecificacionTableroPlantilla traduce el YAML a un modelo comprensible
	// Buscamos trabajar con objetos, no con texto. Evitamos que el dominio entienda
	// YAML
	private final String nombrePlantilla;
	private final List<EspecificacionListaPlantilla> listas;

	@JsonCreator
	public EspecificacionTableroPlantilla(@JsonProperty("nombrePlantilla") String nombrePlantilla, @JsonProperty("listas") List<EspecificacionListaPlantilla> listas) {
		this.nombrePlantilla = nombrePlantilla;
		// Sin listas por defecto
		this.listas = listas == null ? List.of() : List.copyOf(listas);
	}

	public String getNombrePlantilla() {
		return nombrePlantilla;
	}

	public List<EspecificacionListaPlantilla> getListas() {
		return listas;
	}

	// Clase estática que creamos que contiene los detalles de la lista
	public static class EspecificacionListaPlantilla {
		private final String nombre;
		private final Integer limite;
		private final List<String> prerrequisitos;
		private final boolean especial;
		// Esta, a su vez, contiene los detalles de las tarjetas
		private final List<EspecificacionTarjetaPlantilla> tarjetas;

		@JsonCreator
		public EspecificacionListaPlantilla(@JsonProperty("nombre") String nombre, @JsonProperty("limite") Integer limite, @JsonProperty("prerrequisitos") List<String> prerrequisitos,
				@JsonProperty("especial") boolean especial, @JsonProperty("tarjetas") List<EspecificacionTarjetaPlantilla> tarjetas) {
			this.nombre = nombre;
			this.limite = limite;
			// Sin prerrequisitos por defecto
			this.prerrequisitos = prerrequisitos == null ? List.of() : List.copyOf(prerrequisitos);
			this.especial = especial;
			// Sin tarjetas por defecto
			this.tarjetas = tarjetas == null ? List.of() : List.copyOf(tarjetas);
		}

		public String getNombre() {
			return nombre;
		}

		public Integer getLimite() {
			return limite;
		}

		public List<String> getPrerrequisitos() {
			return prerrequisitos;
		}

		public boolean isEspecial() {
			return especial;
		}

		public List<EspecificacionTarjetaPlantilla> getTarjetas() {
			return tarjetas;
		}
	}

	// Clase estática que creamos que contiene los detalles de la tarjeta
	public static class EspecificacionTarjetaPlantilla {
		private final String titulo;
		private final TipoContenidoTarjeta tipoContenido;
		private final String descripcionTarea;
		private final List<String> itemsChecklist;

		@JsonCreator
		public EspecificacionTarjetaPlantilla(@JsonProperty("titulo") String titulo, @JsonProperty("tipoContenido") TipoContenidoTarjeta tipoContenido,
				@JsonProperty("descripcionTarea") String descripcionTarea, @JsonProperty("itemsChecklist") List<String> itemsChecklist) {
			this.titulo = titulo;
			this.tipoContenido = tipoContenido;
			this.descripcionTarea = descripcionTarea;
			// Sin items en la checklist por defecto
			this.itemsChecklist = itemsChecklist == null ? List.of() : List.copyOf(itemsChecklist);
		}

		public String getTitulo() {
			return titulo;
		}

		public TipoContenidoTarjeta getTipoContenido() {
			return tipoContenido;
		}

		public String getDescripcionTarea() {
			return descripcionTarea;
		}

		public List<String> getItemsChecklist() {
			return itemsChecklist;
		}
	}
}