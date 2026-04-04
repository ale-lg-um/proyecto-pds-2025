package es.um.pds.tarjetas.adapters.jpa.entity;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA para persistir sesiones de usuario
 * 
 * - Cada sesión se identifica por un token único
 * - Asociada a un usuario
 * - Tiene fecha de expiración renovable
 */
@Entity
@Table(name = "sesiones")
public class SesionEntity {

	/**
	 * El token es la clave primaria
	 */
	@Id
	@Column(name = "token", nullable = false, length = 255)
	private String token;

	@Column(name = "usuario_id", nullable = false, length = 320)
	private String usuarioId;

	@Column(name = "expira_en", nullable = false)
	private Instant expiraEn;

	public SesionEntity() {
	}

	public SesionEntity(String token, String usuarioId, Instant expiraEn) {
		this.token = token;
		this.usuarioId = usuarioId;
		this.expiraEn = expiraEn;
	}

	// Getters y setters

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Instant getExpiraEn() {
		return expiraEn;
	}

	public void setExpiraEn(Instant expiraEn) {
		this.expiraEn = expiraEn;
	}

	// equals y hashCode por token

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SesionEntity that))
			return false;
		return Objects.equals(token, that.token);
	}

	@Override
	public int hashCode() {
		return Objects.hash(token);
	}
}