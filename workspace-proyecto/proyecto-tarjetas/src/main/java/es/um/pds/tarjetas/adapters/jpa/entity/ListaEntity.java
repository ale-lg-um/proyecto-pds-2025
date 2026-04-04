package es.um.pds.tarjetas.adapters.jpa.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

/**
 * Entidad JPA para persistir el agregado Lista. Usamos tipos primitivos
 * como String, boolean e Integer para aislarse completamente del dominio y así
 * no depender de la infraestructura. Podrían usarse los VO
 */
@Entity
@Table(name = "listas")
public class ListaEntity {

	@Id
	@Column(name = "id", nullable = false, length = 64)
	private String id;

	@Column(name = "nombre_lista", nullable = false)
	private String nombreLista;

	@Column(name = "especial", nullable = false)
	private boolean especial;

	@Column(name = "limite")
	private Integer limite;

	@Column(name = "tablero_id", length = 64)
	private String tableroId;

	/*
	 * Colección de valores tratada como uno a muchos con una nueva tabla
	 * Tarjetas que pertenecen a la lista
	 */
	@ElementCollection
	@CollectionTable(name = "lista_tarjetas_orden", joinColumns = @JoinColumn(name = "lista_id"))
	@OrderColumn(name = "posicion")
	@Column(name = "tarjeta_id", nullable = false, length = 64)
	private List<String> listaTarjetas = new ArrayList<>();

	/*
	 * Colección de valores tratada como uno a muchos con una nueva tabla
	 * Listas por las que tiene que pasar una tarjeta antes de entrar a esta lista
	 */
	@ElementCollection
	@CollectionTable(name = "lista_prerrequisitos", joinColumns = @JoinColumn(name = "lista_id"))
	@Column(name = "prerrequisito_id", nullable = false, length = 64)
	private Set<String> prerrequisitos = new HashSet<>();

	public ListaEntity() {
	}

	public ListaEntity(String id, String nombreLista, boolean especial, Integer limite, String tableroId,
			List<String> listaTarjetas, Set<String> prerrequisitos) {
		this.id = id;
		this.nombreLista = nombreLista;
		this.especial = especial;
		this.limite = limite;
		this.tableroId = tableroId;
		this.listaTarjetas = listaTarjetas != null ? new ArrayList<>(listaTarjetas) : new ArrayList<>();
		this.prerrequisitos = prerrequisitos != null ? new HashSet<>(prerrequisitos) : new HashSet<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombreLista() {
		return nombreLista;
	}

	public void setNombreLista(String nombreLista) {
		this.nombreLista = nombreLista;
	}

	public boolean isEspecial() {
		return especial;
	}

	public void setEspecial(boolean especial) {
		this.especial = especial;
	}

	public Integer getLimite() {
		return limite;
	}

	public void setLimite(Integer limite) {
		this.limite = limite;
	}

	public String getTableroId() {
		return tableroId;
	}

	public void setTableroId(String tableroId) {
		this.tableroId = tableroId;
	}

	public List<String> getListaTarjetas() {
		return listaTarjetas;
	}

	public void setListaTarjetas(List<String> listaTarjetas) {
		this.listaTarjetas = listaTarjetas != null ? listaTarjetas : new ArrayList<>();
	}

	public Set<String> getPrerrequisitos() {
		return prerrequisitos;
	}

	public void setPrerrequisitos(Set<String> prerrequisitos) {
		this.prerrequisitos = prerrequisitos != null ? prerrequisitos : new HashSet<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ListaEntity that))
			return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}