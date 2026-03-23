package es.um.pds.tarjetas.domain.ports.input.autenticacion;

import es.um.pds.tarjetas.domain.model.usuario.UsuarioId;

public interface ServicioAutenticacion {
	// Envía el código de login al email del usuario
	void enviarCodigoLogin(UsuarioId email);
	
	// Verifica el código de login con el código enviado al usuario
	String verificarCodigoLogin(UsuarioId email, String codigo);
}
