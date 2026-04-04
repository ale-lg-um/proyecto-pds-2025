package es.um.pds.tarjetas.adapters.jpa.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import es.um.pds.tarjetas.adapters.jpa.embeddable.EtiquetaEmbeddable;
import es.um.pds.tarjetas.adapters.jpa.embeddable.ItemChecklistEmbeddable;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

/**
 * Entidad JPA para persistir el agregado Tarjeta. Usamos tipos primitivos
 * como String y LocalDate para aislarse completamente del dominio y así
 * no depender de la infraestructura. Podrían usarse los VO
 * Se usa esta estrategia para el contenido:
 * tipoContenido = TAREA o CHECKLIST
 * tareaDescripcion para tarjetas de tipo tarea
 * itemsChecklist para tarjetas de tipo checklist
 */
@Entity
@Table(name = "tarjetas")
public class TarjetaEntity {

	@Id
	@Column(name = "id", nullable = false, length = 64)
	private String id;

	@Column(name = "titulo", nullable = false)
	private String titulo;

	@Column(name = "fecha_creacion", nullable = false)
	private LocalDate fechaCreacion;

	@Column(name = "lista_actual_id", nullable = false, length = 64)
	private String listaActualId;

	@Column(name = "tablero_id", length = 64)
	private String tableroId;

	@Column(name = "tipo_contenido", nullable = false, length = 20)
	private String tipoContenido;

	@Column(name = "tarea_descripcion", length = 4000)
	private String tareaDescripcion;

	@Column(name = "completada", nullable = false)
	private boolean completada;

	/*
	 * Colección de valores tratada como uno a muchos con una nueva tabla Etiquetas
	 * que contiene la tarjeta. No hay que usar embedded porque están dentro de una
	 * colección
	 */
	@ElementCollection
	@CollectionTable(name = "tarjeta_etiquetas", joinColumns = @JoinColumn(name = "tarjeta_id"))
	private List<EtiquetaEmbeddable> etiquetas = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "tarjeta_listas_visitadas", joinColumns = @JoinColumn(name = "tarjeta_id"))
	@Column(name = "lista_id", nullable = false, length = 64)
	private Set<String> listasVisitadas = new HashSet<>();

	/*
	 * Colección de valores tratada como uno a muchos con una nueva tabla Ítems que
	 * contiene una checklist. No hay que usar embedded porque están dentro de una
	 * colección
	 */
	@ElementCollection
	@CollectionTable(name = "tarjeta_checklist_items", joinColumns = @JoinColumn(name = "tarjeta_id"))
	@OrderColumn(name = "posicion")
	private List<ItemChecklistEmbeddable> itemsChecklist = new ArrayList<>();

	public TarjetaEntity() {
	}

	public TarjetaEntity(String id, String titulo, LocalDate fechaCreacion, String listaActualId, String tableroId,
			String tipoContenido, String tareaDescripcion, boolean completada, List<EtiquetaEmbeddable> etiquetas,
			Set<String> listasVisitadas, List<ItemChecklistEmbeddable> itemsChecklist) {
		this.id = id;
		this.titulo = titulo;
		this.fechaCreacion = fechaCreacion;
		this.listaActualId = listaActualId;
		this.tableroId = tableroId;
		this.tipoContenido = tipoContenido;
		this.tareaDescripcion = tareaDescripcion;
		this.completada = completada;
		this.etiquetas = etiquetas != null ? new ArrayList<>(etiquetas) : new ArrayList<>();
		this.listasVisitadas = listasVisitadas != null ? new HashSet<>(listasVisitadas) : new HashSet<>();
		this.itemsChecklist = itemsChecklist != null ? new ArrayList<>(itemsChecklist) : new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public LocalDate getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDate fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getListaActualId() {
		return listaActualId;
	}

	public void setListaActualId(String listaActualId) {
		this.listaActualId = listaActualId;
	}

	public String getTableroId() {
		return tableroId;
	}

	public void setTableroId(String tableroId) {
		this.tableroId = tableroId;
	}

	public String getTipoContenido() {
		return tipoContenido;
	}

	public void setTipoContenido(String tipoContenido) {
		this.tipoContenido = tipoContenido;
	}

	public String getTareaDescripcion() {
		return tareaDescripcion;
	}

	public void setTareaDescripcion(String tareaDescripcion) {
		this.tareaDescripcion = tareaDescripcion;
	}

	public boolean isCompletada() {
		return completada;
	}

	public void setCompletada(boolean completada) {
		this.completada = completada;
	}

	public List<EtiquetaEmbeddable> getEtiquetas() {
		return etiquetas;
	}

	public void setEtiquetas(List<EtiquetaEmbeddable> etiquetas) {
		this.etiquetas = etiquetas != null ? etiquetas : new ArrayList<>();
	}

	public Set<String> getListasVisitadas() {
		return listasVisitadas;
	}

	public void setListasVisitadas(Set<String> listasVisitadas) {
		this.listasVisitadas = listasVisitadas != null ? listasVisitadas : new HashSet<>();
	}

	public List<ItemChecklistEmbeddable> getItemsChecklist() {
		return itemsChecklist;
	}

	public void setItemsChecklist(List<ItemChecklistEmbeddable> itemsChecklist) {
		this.itemsChecklist = itemsChecklist != null ? itemsChecklist : new ArrayList<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TarjetaEntity that))
			return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}