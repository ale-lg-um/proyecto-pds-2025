package es.um.pds.tarjetas.domain.model.usuario;

import java.util.Objects;

//@Entity
public class Usuario {
	// Atributos
	private UsuarioId id;
	private String nombre;
	
	// Constructor
	public Usuario(UsuarioId id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}
	
	// Getters
	public UsuarioId getId() {
		return this.id;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	// Overrides
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(!(obj instanceof Usuario)) {
			return false;
		}
		Usuario other = (Usuario) obj;
		return Objects.equals(this.id, other.id);
	}
}
