package es.um.pds.tarjetas.adapters.jpa.embeddable;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Tipo embebido JPA para persistir una etiqueta. Se guarda el nombre y una
 * representación con String del color
 */
@Embeddable
public class EtiquetaEmbeddable {

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "color", nullable = false)
	private String color;

	public EtiquetaEmbeddable() {
	}

	public EtiquetaEmbeddable(String nombre, String color) {
		this.nombre = nombre;
		this.color = color;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof EtiquetaEmbeddable that))
			return false;
		return Objects.equals(nombre, that.nombre) && Objects.equals(color, that.color);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nombre, color);
	}
}