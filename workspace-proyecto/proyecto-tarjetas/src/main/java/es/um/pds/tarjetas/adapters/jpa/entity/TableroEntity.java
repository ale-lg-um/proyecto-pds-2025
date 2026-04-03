package es.um.pds.tarjetas.adapters.jpa.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import es.um.pds.tarjetas.adapters.jpa.embeddable.EstadoBloqueoEmbeddable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

/**
 * Entidad JPA para persistir el agregado Tablero
 */
@Entity
@Table(name = "tableros")
public class TableroEntity {

	@Id
	@Column(name = "id", nullable = false, length = 64)
	private String id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "token_url", nullable = false, unique = true)
	private String tokenUrl;

	@Column(name = "creador_id", nullable = false, length = 320)
	private String creadorId;

	@Column(name = "lista_especial_id", length = 64)
	private String listaEspecialId;

	/*
	 * Colección de valores tratada como uno a muchos con una nueva tabla
	 * Listas que pertenecen al tablero
	 */
	@ElementCollection
	@CollectionTable(name = "tablero_listas", joinColumns = @JoinColumn(name = "tablero_id"))
	@Column(name = "lista_id", nullable = false, length = 64)
	private Set<String> listas = new HashSet<>();

	/* 
	 * El estado de bloqueo forma parte del tablero, no tiene identidad propia, es un VO
	 * Por eso utilizamos @Embedded para integrarlo en la entidad, guardando sus atributos
	 * como columnas de la tabla principal, para lo que utilizamos @AttributeOverride,
	 * mapeando el nombre del atributo con la nueva columna
	 */
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "desde", column = @Column(name = "bloqueo_desde")),
			@AttributeOverride(name = "hasta", column = @Column(name = "bloqueo_hasta")),
			@AttributeOverride(name = "descripcion", column = @Column(name = "bloqueo_descripcion")) })
	private EstadoBloqueoEmbeddable estadoBloqueo;

	public TableroEntity() {
	}

	public TableroEntity(String id, String nombre, String tokenUrl, String creadorId, String listaEspecialId,
			Set<String> listas, EstadoBloqueoEmbeddable estadoBloqueo) {
		this.id = id;
		this.nombre = nombre;
		this.tokenUrl = tokenUrl;
		this.creadorId = creadorId;
		this.listaEspecialId = listaEspecialId;
		this.listas = listas != null ? new HashSet<>(listas) : new HashSet<>();
		this.estadoBloqueo = estadoBloqueo;
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

	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	public String getCreadorId() {
		return creadorId;
	}

	public void setCreadorId(String creadorId) {
		this.creadorId = creadorId;
	}

	public String getListaEspecialId() {
		return listaEspecialId;
	}

	public void setListaEspecialId(String listaEspecialId) {
		this.listaEspecialId = listaEspecialId;
	}

	public Set<String> getListas() {
		return listas;
	}

	public void setListas(Set<String> listas) {
		this.listas = listas != null ? listas : new HashSet<>();
	}

	public EstadoBloqueoEmbeddable getEstadoBloqueo() {
		return estadoBloqueo;
	}

	public void setEstadoBloqueo(EstadoBloqueoEmbeddable estadoBloqueo) {
		this.estadoBloqueo = estadoBloqueo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TableroEntity that))
			return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}