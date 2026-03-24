package es.um.pds.tarjetas.application.common;

import java.util.List;

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;

//TODO Revisar
public class EspecificacionTableroPlantilla {

	private final String nombrePlantilla;
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
		private final List<ListaId> prerrequisitos;
		private final boolean especial;
	
		public EspecificacionListaPlantilla(String nombre, Integer limite, List<ListaId> prerrequisitos, boolean especial) {
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
	
	    public List<ListaId> getPrerrequisitos() {
	    	return prerrequisitos;
	    }
	
	    public boolean isEspecial() {
	    	return especial;
	    }
	}
}