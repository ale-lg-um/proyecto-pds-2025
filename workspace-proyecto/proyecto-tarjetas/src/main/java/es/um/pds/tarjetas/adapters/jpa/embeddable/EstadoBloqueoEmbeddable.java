package es.um.pds.tarjetas.adapters.jpa.embeddable;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Tipo embebido JPA para persistir el estado de bloqueo de un tablero
 * Se anota con "@Embeddable"
 */
@Embeddable
public class EstadoBloqueoEmbeddable {

	@Column(name = "desde")
	private LocalDate desde;

	@Column(name = "hasta")
	private LocalDate hasta;

	@Column(name = "descripcion")
	private String descripcion;

	public EstadoBloqueoEmbeddable() {
	}

	public EstadoBloqueoEmbeddable(LocalDate desde, LocalDate hasta, String descripcion) {
		this.desde = desde;
		this.hasta = hasta;
		this.descripcion = descripcion;
	}

	public LocalDate getDesde() {
		return desde;
	}

	public void setDesde(LocalDate desde) {
		this.desde = desde;
	}

	public LocalDate getHasta() {
		return hasta;
	}

	public void setHasta(LocalDate hasta) {
		this.hasta = hasta;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}