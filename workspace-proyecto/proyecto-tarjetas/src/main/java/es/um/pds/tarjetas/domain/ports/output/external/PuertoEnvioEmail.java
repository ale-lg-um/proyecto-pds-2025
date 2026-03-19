package es.um.pds.tarjetas.domain.ports.output.external;

import es.um.pds.tarjetas.domain.model.usuario.UsuarioId;

public interface PuertoEnvioEmail {

	void enviarEmail(UsuarioId destinatario, String asunto, String cuerpo);
}