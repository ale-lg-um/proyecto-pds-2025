package es.um.pds.tarjetas.adapters.jpa.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA para persistir usuarios. El id coincide con el email del usuario
 */
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

	@Id
	@Column(name = "id", nullable = false, length = 320)
	private String id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	public UsuarioEntity() {
	}

	public UsuarioEntity(String id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof UsuarioEntity that))
			return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}