package es.um.pds.tarjetas.domain.model.usuario.model;

import java.util.Objects;

import es.um.pds.tarjetas.application.common.exceptions.UsuarioInvalidoException;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public class Usuario {
	// Atributos
	private final UsuarioId identificador;
	private String nombre;
	
	// Constructor
	private Usuario(UsuarioId identificador, String nombre) {
		this.identificador = identificador;
		this.nombre = nombre;
	}
	
	// Método factoría y reconstrucción
	public static Usuario of(UsuarioId identificador, String nombre) {
		if (identificador == null) {
			throw new UsuarioInvalidoException("El usuario debe tener un correo electrónico");
		}
		
		if (nombre == null || nombre.isBlank()) {
			throw new UsuarioInvalidoException("El nombre de usuario no puede estar vacío");
		}
		
		return new Usuario(identificador, nombre);
	}
	
	// Getters
	public UsuarioId getIdentificador() {
		return this.identificador;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	// Overrides
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(!(obj instanceof Usuario)) {
			return false;
		}
		Usuario other = (Usuario) obj;
		return Objects.equals(this.identificador, other.identificador);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(identificador);
	}
	
	// Funcionalidades
	public void cambiarNombre(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
		}
		this.nombre = nombre;
	}
}
