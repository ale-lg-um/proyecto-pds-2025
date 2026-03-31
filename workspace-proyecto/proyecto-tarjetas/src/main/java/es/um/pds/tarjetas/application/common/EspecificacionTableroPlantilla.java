package es.um.pds.tarjetas.application.common;

import java.util.List;

public class EspecificacionTableroPlantilla {

	// EspecificacionTableroPlantilla traduce el YAML a un modelo comprensible
	// Buscamos trabajar con objetos, no con texto. Evitamos que el dominio entienda YAML
	
	private final String nombrePlantilla;
	// Clase estática que creamos que contiene los detalles de la lista
	private final List<EspecificacionListaPlantilla> listas;
	
	public EspecificacionTableroPlantilla(String nombrePlantilla, List<EspecificacionListaPlantilla> listas) {
		this.nombrePlantilla = nombrePlantilla;
		this.listas = listas;
	}
	
	public String getNombrePlantilla() {
		return nombrePlantilla;
	}
	
	public List<EspecificacionListaPlantilla> getListas() {
		return listas;
	}
	
	public static class EspecificacionListaPlantilla {
		private final String nombre;
		private final Integer limite;
		private final List<String> prerrequisitos;
		private final boolean especial;
	
		public EspecificacionListaPlantilla(String nombre, Integer limite, List<String> prerrequisitos, boolean especial) {
			this.nombre = nombre;
	    	this.limite = limite;
	    	this.prerrequisitos = prerrequisitos;
	    	this.especial = especial;
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
	}
}