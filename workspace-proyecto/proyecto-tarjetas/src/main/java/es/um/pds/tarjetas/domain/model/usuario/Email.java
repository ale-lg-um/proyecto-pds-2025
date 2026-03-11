package es.um.pds.tarjetas.domain.model.usuario;

import java.util.Objects;

public class Email {
	// Atributos
	private String direccion; // Dirección de correo electrónico
	
	// Excepción
	public static class EmailInvalidoException extends Exception {
		public EmailInvalidoException(String mensaje) {
			super(mensaje);
		}
	}
	
	// Constructor
	private Email(String direccion) {
		this.direccion = direccion;
	}
	
	// Método 'of' utilizando patrón creador y método factoría
	// Dudo de si esto es correcto
	public static Email of(String direccion) throws EmailInvalidoException {
		if(direccion == null || direccion.isBlank() || !direccion.contains("@")) { // Si la dirección es nula o no tiene el formato correcto
			throw new EmailInvalidoException("El formato del email no es válido");
		}
		return new Email(direccion);
	}
	
	// Getters
	public String getDireccion() {
		return this.direccion;
	}
	
	// Overrides
	@Override
	public int hashCode() {
		return Objects.hash(direccion);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(!(obj instanceof Email)) {
			return false;
		}
		Email other = (Email) obj;
		return Objects.equals(this.direccion, other.direccion);
	}
}
