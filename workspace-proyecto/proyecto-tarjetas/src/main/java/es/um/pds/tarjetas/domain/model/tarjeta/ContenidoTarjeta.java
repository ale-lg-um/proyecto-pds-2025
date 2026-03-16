package es.um.pds.tarjetas.domain.model.tarjeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ContenidoTarjeta {
	// Atributos
	private final String tipo;					// Puede ser 'TAREA' o 'CHECKLIST'
	private final List<Tarea> elementos;		// Texto de la tarea o ítems del checklist
	
	// Constructor
	private ContenidoTarjeta(String tipo, List<Tarea> elementos) {
		this.tipo = tipo;
		this.elementos = new ArrayList<>(elementos);
	}
	
	// Método 'of'
	public static ContenidoTarjeta of(String tipo, List<Tarea> elementos) {
		if(tipo == null || (!tipo.equals("TAREA") && !tipo.equals("CHECKLIST"))) {
			throw new IllegalArgumentException("La tarjeta debe ser de tipo Tarea o Checklist.");
		} else if(elementos == null || elementos.isEmpty()) {
			throw new IllegalArgumentException("El contenido debe tener al menos un elemento");
		}
		
		return new ContenidoTarjeta(tipo, elementos);
	}
	
	// Getters
	public String getTipo() {
		return this.tipo;
	}
	
	public List<Tarea> getElementos() {
		return Collections.unmodifiableList(this.elementos);
	}
	
	// Overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ContenidoTarjeta that = (ContenidoTarjeta) o;
		return tipo.equals(that.tipo) && elementos.equals(that.elementos);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tipo, elementos);
	}
}
