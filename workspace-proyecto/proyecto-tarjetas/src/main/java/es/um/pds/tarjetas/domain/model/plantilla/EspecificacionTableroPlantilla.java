package es.um.pds.tarjetas.domain.model.plantilla;

import java.util.List;

import es.um.pds.tarjetas.domain.model.tarjeta.model.TipoContenidoTarjeta;

public class EspecificacionTableroPlantilla {

	// EspecificacionTableroPlantilla traduce el YAML a un modelo comprensible
	// Buscamos trabajar con objetos, no con texto. Evitamos que el dominio entienda
	// YAML
	private final String nombrePlantilla;
	private final List<EspecificacionListaPlantilla> listas;

	public EspecificacionTableroPlantilla(String nombrePlantilla, List<EspecificacionListaPlantilla> listas) {
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

		public EspecificacionListaPlantilla(String nombre, Integer limite, List<String> prerrequisitos,
				boolean especial, List<EspecificacionTarjetaPlantilla> tarjetas) {
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

		public EspecificacionTarjetaPlantilla(String titulo, TipoContenidoTarjeta tipoContenido,
				String descripcionTarea, List<String> itemsChecklist) {
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