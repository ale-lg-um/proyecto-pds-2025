package es.um.pds.tarjetas.domain.model.tarjeta.model;

public abstract class ContenidoTarjeta {
	
	// Constructor protegido para evitar instanciación directa
	protected ContenidoTarjeta() {
	}
	
	/**
	 * Devuelve el tipo de contenido (TAREA o CHECKLIST)
	 * Se implementa en las subclases, útil para mapeo JPA
	 */
	public abstract TipoContenidoTarjeta getTipo();
	
}