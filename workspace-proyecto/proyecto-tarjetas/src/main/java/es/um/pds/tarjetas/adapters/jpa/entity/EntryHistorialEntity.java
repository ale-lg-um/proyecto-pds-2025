package es.um.pds.tarjetas.adapters.jpa.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * Entidad JPA para persistir entradas del historial. Usamos tipos primitivos
 * como String y LocalDate para aislarse completamente del dominio y así
 * no depender de la infraestructura. Podrían usarse los VO
 */
@Entity
@Table(name = "entries_historial")
public class EntryHistorialEntity {

	@Id
	@Column(name = "id", nullable = false, length = 64)
	private String id;

	@Column(name = "tablero_id", nullable = false, length = 64)
	private String tableroId;

	@Column(name = "tipo", nullable = false, length = 100)
	private String tipo;

	@Column(name = "email_actor", nullable = false, length = 320)
	private String emailActor;

	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;

	@Lob
	@Column(name = "detalles", nullable = false)
	private String detalles;

	public EntryHistorialEntity() {
	}

	public EntryHistorialEntity(String id, String tableroId, String tipo, String emailActor, LocalDateTime timestamp,
			String detalles) {
		this.id = id;
		this.tableroId = tableroId;
		this.tipo = tipo;
		this.emailActor = emailActor;
		this.timestamp = timestamp;
		this.detalles = detalles;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTableroId() {
		return tableroId;
	}

	public void setTableroId(String tableroId) {
		this.tableroId = tableroId;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getEmailActor() {
		return emailActor;
	}

	public void setEmailActor(String emailActor) {
		this.emailActor = emailActor;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getDetalles() {
		return detalles;
	}

	public void setDetalles(String detalles) {
		this.detalles = detalles;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof EntryHistorialEntity that))
			return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}