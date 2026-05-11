package es.um.pds.tarjetas.domain.ports.output;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public interface PuertoEnvioEmail {

	void enviarEmail(UsuarioId destinatario, String asunto, String cuerpo);
}