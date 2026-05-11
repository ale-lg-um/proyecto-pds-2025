package es.um.pds.tarjetas.adapters.jpa.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * Entidad JPA para persistir plantillas. Usamos tipos primitivos
 * como String para aislarse completamente del dominio y así
 * no depender de la infraestructura. Podrían usarse los VO
 */
@Entity
@Table(name = "plantillas")
public class PlantillaEntity {

	@Id
	@Column(name = "id", nullable = false, length = 64)
	private String id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Lob
	@Column(name = "contenido_yaml", nullable = false)
	private String contenidoYaml;

	public PlantillaEntity() {
	}

	public PlantillaEntity(String id, String nombre, String contenidoYaml) {
		this.id = id;
		this.nombre = nombre;
		this.contenidoYaml = contenidoYaml;
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

	public String getContenidoYaml() {
		return contenidoYaml;
	}

	public void setContenidoYaml(String contenidoYaml) {
		this.contenidoYaml = contenidoYaml;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PlantillaEntity that))
			return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}