package es.um.pds.tarjetas.adapters.mappers;

import es.um.pds.tarjetas.adapters.jpa.entity.UsuarioEntity;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.model.usuario.model.Usuario;

/**
 * Mapper entre Usuario del dominio y UsuarioEntity de JPA
 */
public class UsuarioMapperJPA {

	private UsuarioMapperJPA() {
	}

	/*
	 * Versión anterior:
	public static UsuarioEntity toEntity(Usuario d) {
		return new UsuarioEntity(d.getIdentificador().toString(), d.getNombre());
	}
	*/
	public static UsuarioEntity toEntity(Usuario d) {
		return new UsuarioEntity(d.getIdentificador().getCorreo(), d.getNombre());
	}

	public static Usuario toDomain(UsuarioEntity e) {
		return Usuario.of(UsuarioId.of(e.getId()), e.getNombre());
	}
}