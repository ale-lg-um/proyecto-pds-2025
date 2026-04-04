package es.um.pds.tarjetas.adapters.jpa.entity;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA para persistir los códigos de login temporales.
 * 
 * - Un usuario tiene como mucho un código activo
 * - Se guarda el código y su fecha de expiración
 */
@Entity
@Table(name = "codigos_login")
public class CodigoLoginEntity {

	/**
	 * Usamos el usuarioId como clave primaria Garantiza 1 código activo por usuario
	 */
	@Id
	@Column(name = "usuario_id", nullable = false, length = 320)
	private String usuarioId;

	@Column(name = "codigo", nullable = false, length = 20)
	private String codigo;

	// Instant mejor que LocalDateTime para la BD
	@Column(name = "expira_en", nullable = false)
	private Instant expiraEn;

	public CodigoLoginEntity() {
	}

	public CodigoLoginEntity(String usuarioId, String codigo, Instant expiraEn) {
		this.usuarioId = usuarioId;
		this.codigo = codigo;
		this.expiraEn = expiraEn;
	}

	// Getters y setters

	public String getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Instant getExpiraEn() {
		return expiraEn;
	}

	public void setExpiraEn(Instant expiraEn) {
		this.expiraEn = expiraEn;
	}

	// equals y hashCode por id (usuarioId)

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CodigoLoginEntity that))
			return false;
		return Objects.equals(usuarioId, that.usuarioId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(usuarioId);
	}
}