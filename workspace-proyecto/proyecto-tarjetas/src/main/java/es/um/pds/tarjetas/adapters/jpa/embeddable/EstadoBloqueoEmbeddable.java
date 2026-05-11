package es.um.pds.tarjetas.adapters.jpa.embeddable;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Tipo embebido JPA para persistir el estado de bloqueo de un tablero
 * Se anota con "@Embeddable"
 */
@Embeddable
public class EstadoBloqueoEmbeddable {

	@Column(name = "desde")
	private LocalDateTime desde;

	@Column(name = "hasta")
	private LocalDateTime hasta;

	@Column(name = "descripcion")
	private String descripcion;

	// Constructor vacío
	public EstadoBloqueoEmbeddable() {
	}

	// Constructor con argumentos
	public EstadoBloqueoEmbeddable(LocalDateTime desde, LocalDateTime hasta, String descripcion) {
		this.desde = desde;
		this.hasta = hasta;
		this.descripcion = descripcion;
	}

	// Getters y setters
	
	public LocalDateTime getDesde() {
		return desde;
	}

	public void setDesde(LocalDateTime desde) {
		this.desde = desde;
	}

	public LocalDateTime getHasta() {
		return hasta;
	}

	public void setHasta(LocalDateTime hasta) {
		this.hasta = hasta;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}