package es.um.pds.tarjetas.domain.model.tarjeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.um.pds.tarjetas.domain.model.lista.ListaId;

//@Entity
public class Tarjeta {
	// Atributos
	private TarjetaId identificador;
	private String tipo;				// Puede ser una lista de tareas o un checklist.
	private List<Tarea> tareas;			// Lista de tareas que contiene la tarjeta.
	private Set<Etiqueta> etiquetas;	// Conjunto de etiquetas de la tarjeta.
	private boolean completada;
	private Set<ListaId> listasVisitadas;	// Conjunto de listas por las que ha pasado la tarjeta.
	
	// Excepción
	public static class TarjetaInvalidaException extends Exception {
		public TarjetaInvalidaException(String mensaje) {
			super(mensaje);
		}
	}
	
	// Constructor
	private Tarjeta(TarjetaId identificador, String tipo, List<Tarea> tareas) {
		this.identificador = identificador;
		this.tipo = tipo;
		this.tareas = new ArrayList<>(tareas);
		this.etiquetas = new HashSet<>();
		this.completada = false;
	}
	
	// Método factoría
	public static Tarjeta of(TarjetaId identificador, String tipo, List<Tarea> tareas) throws TarjetaInvalidaException {
        if (identificador == null) {
            throw new TarjetaInvalidaException("La tarjeta debe tener un identificador.");
        }
        if (!tipo.equals("TAREA") && !tipo.equals("CHECKLIST")) {
            throw new TarjetaInvalidaException("El tipo debe ser TAREA o CHECKLIST.");
        }
        if (tareas == null || tareas.isEmpty()) {
            throw new TarjetaInvalidaException("La tarjeta debe contener al menos una tarea.");
        }
        if (tipo.equals("TAREA") && tareas.size() > 1) {
            throw new TarjetaInvalidaException("Una tarjeta de tipo TAREA solo puede tener 1 tarea.");
        }
        
        return new Tarjeta(identificador, tipo, tareas);
    }
	
	// Getters y setters
	public TarjetaId getIdentificador() {
		return this.identificador;
	}
	
	public String getTipo() {
		return this.tipo;
	}
	
	public List<Tarea> getTareas() {
		return this.tareas;
	}
	
	public void setContenido(ContenidoTarjeta contenido) {
		this.tipo = contenido.getTipo();
		this.tareas = new ArrayList<>(contenido.getElementos());
	}
	
	public Set<Etiqueta> getEtiquetas() {
		return Collections.unmodifiableSet(this.etiquetas);
	}
	
	public boolean isCompletada() {
		return this.completada;
	}
	
	// Funcionalidades
	public void completar() {
		this.completada = true;
	}
	
	public void anadirEtiqueta(Etiqueta nueva) {
		if(this.isCompletada()) {
			throw new IllegalStateException("No es posible  añadir etiquetas a una tarjeta completada");
		}
		
		this.etiquetas.add(nueva);
	}
	
	public void eliminarEtiqueta(Etiqueta eliminada) {
		this.etiquetas.remove(eliminada);
	}
}
