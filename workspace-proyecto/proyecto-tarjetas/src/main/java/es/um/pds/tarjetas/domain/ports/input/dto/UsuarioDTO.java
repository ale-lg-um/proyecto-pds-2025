package es.um.pds.tarjetas.domain.ports.input.dto;

import es.um.pds.tarjetas.domain.model.usuario.model.Usuario;

public record UsuarioDTO(String email, String nombre) {
	public UsuarioDTO(Usuario usuario) {
		this(usuario.getIdentificador().getCorreo(), usuario.getNombre());
	}
}