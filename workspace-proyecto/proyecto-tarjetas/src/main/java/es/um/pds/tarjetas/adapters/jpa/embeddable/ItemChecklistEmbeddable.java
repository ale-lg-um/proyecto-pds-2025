package es.um.pds.tarjetas.adapters.jpa.embeddable;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Tipo embebido JPA para persistir un ítem de checklist
 */
@Embeddable
public class ItemChecklistEmbeddable {

	@Column(name = "descripcion", nullable = false)
	private String descripcion;

	@Column(name = "completado", nullable = false)
	private boolean completado;

	public ItemChecklistEmbeddable() {
	}

	public ItemChecklistEmbeddable(String descripcion, boolean completado) {
		this.descripcion = descripcion;
		this.completado = completado;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public boolean isCompletado() {
		return completado;
	}

	public void setCompletado(boolean completado) {
		this.completado = completado;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ItemChecklistEmbeddable that))
			return false;
		return completado == that.completado && Objects.equals(descripcion, that.descripcion);
	}

	@Override
	public int hashCode() {
		return Objects.hash(descripcion, completado);
	}
}