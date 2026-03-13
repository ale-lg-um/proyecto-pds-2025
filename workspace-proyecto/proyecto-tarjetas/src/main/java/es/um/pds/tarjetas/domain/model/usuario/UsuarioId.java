package es.um.pds.tarjetas.domain.model.usuario;

import java.util.Objects;

public class UsuarioId {
	// Atributos
	private String correo;
	
	// Excepción
	public static class IdentificadorUsuarioException extends Exception {
		public IdentificadorUsuarioException(String mensaje) {
			super(mensaje);
		}
	}
	
	// Constructor
	private UsuarioId(String correo) {
		this.correo = correo;
	}
	
	// Método 'of'
	public static UsuarioId of(String correo) throws IdentificadorUsuarioException {
		if(correo == null || correo.isBlank() || !correo.contains("@")) {
			throw new IdentificadorUsuarioException("El correo no tiene el formato correcto.");
		}
		return new UsuarioId(correo);
	}
	
	// Getters
	public String getCorreo() {
		return this.correo;
	}
	
	// Overrides
	@Override
	public int hashCode() {
		return Objects.hash(correo);
	}
	
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
}
