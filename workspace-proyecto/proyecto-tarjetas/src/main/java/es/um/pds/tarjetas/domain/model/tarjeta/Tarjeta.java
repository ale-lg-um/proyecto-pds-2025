package es.um.pds.tarjetas.domain.model.tarjeta;

import java.util.List;

import es.um.pds.tarjetas.domain.model.tarea.Tarea;

//@Entity
public class Tarjeta {
	// Atributos
	private TarjetaId identificador;
	private String tipo;				// Puede ser una lista de tareas o un checklist.
	private List<Tarea> tareas;			// Lista de tareas que contiene la tarjeta.
	private boolean completada;
	
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
		this.tareas = tareas;
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
	
	// Getters
	public TarjetaId getIdentificador() {
		return this.identificador;
	}
	
	public String gettipo() {
		return this.tipo;
	}
	
	public List<Tarea> getTareas() {
		return this.tareas;
	}
	
	public boolean isCompletada() {
		return this.completada;
	}
}
