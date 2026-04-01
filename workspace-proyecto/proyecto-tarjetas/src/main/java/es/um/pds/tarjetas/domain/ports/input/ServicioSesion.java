package es.um.pds.tarjetas.domain.ports.input;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

// Aunque se podría incluir dentro de ServicioAutenticacion, lo separamos para diferenciar mejor la semántica en el DDD.
// TODO A este servicio llamarlo desde filtro HTTP
public interface ServicioSesion {
	public UsuarioId validarYRenovarToken(String token);
}
