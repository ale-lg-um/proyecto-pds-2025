package es.um.pds.tarjetas.domain.model.usuario;

import java.util.Objects;
import java.util.regex.Pattern;

import es.um.pds.tarjetas.application.common.exceptions.UsuarioInvalidoException;

public class UsuarioId {
	// Constantes
	// Regex para correos electrónicos
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	
	// Atributos
	private final String correo;
	
	// Constructor
	private UsuarioId(String correo) {
		this.correo = correo;
	}
	
	// Método factoría con identificador
	public static UsuarioId of(String correo) throws UsuarioInvalidoException {
		if(correo == null || correo.isBlank()) {
			throw new UsuarioInvalidoException("El correo no puede ser nulo o vacío");
		}
		
		String correoNormalizado = correo.trim().toLowerCase();

		if (!EMAIL_PATTERN.matcher(correoNormalizado).matches()) {
			throw new UsuarioInvalidoException("El correo no tiene un formato válido.");
		}

		return new UsuarioId(correoNormalizado);
	}
	
	// Getters
	public String getCorreo() {
		return this.correo;
	}
	
	// Overrides
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(!(obj instanceof UsuarioId)) {
			return false;
		}
		UsuarioId other = (UsuarioId) obj;
		return Objects.equals(this.correo, other.correo);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(correo);
	}
}
