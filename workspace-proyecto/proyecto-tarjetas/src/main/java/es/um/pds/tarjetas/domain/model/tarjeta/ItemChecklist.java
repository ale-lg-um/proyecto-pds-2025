package es.um.pds.tarjetas.domain.model.tarjeta;

public class ItemChecklist {

	// Atributos
	private final String descripcion;
	private boolean completado;

	// Constructor
	private ItemChecklist(String descripcion, boolean completado) {
		this.descripcion = descripcion;
		this.completado = completado;
	}

	// Método factoría
	public static ItemChecklist of(String descripcion) {
		if (descripcion == null || descripcion.isBlank()) {
			throw new IllegalArgumentException("La descripción del ítem no puede estar vacía");
		}
		return new ItemChecklist(descripcion, false);
	}

	// Getters
	public String getDescripcion() {
		return descripcion;
	}
	
	// No redefinimos equals y hashCode porque podemos tener ItemsChecklist con la misma
	// descripción. Solo comparamos por oid

	public boolean isCompletado() {
		return completado;
	}

	// Comportamiento de dominio
	public void marcarComoCompletado() {
		this.completado = true;
	}

	public void marcarComoPendiente() {
		this.completado = false;
	}
}