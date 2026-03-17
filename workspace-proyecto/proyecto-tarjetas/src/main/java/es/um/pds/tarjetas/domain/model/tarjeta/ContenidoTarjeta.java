package es.um.pds.tarjetas.domain.model.tarjeta;

public abstract class ContenidoTarjeta {
	
	public enum TipoContenido {
		TAREA,
		CHECKLIST
	}
	
	// Constructor protegido para evitar instanciación directa
	protected ContenidoTarjeta() {
	}
	
	/**
	 * Devuelve el tipo de contenido (TAREA o CHECKLIST)
	 * Se implementa en las subclases, útil para mapeo JPA
	 */
	public abstract TipoContenido getTipo();
	
}