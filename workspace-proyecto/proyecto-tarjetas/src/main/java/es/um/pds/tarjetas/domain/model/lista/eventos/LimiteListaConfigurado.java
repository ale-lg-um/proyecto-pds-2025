package es.um.pds.tarjetas.domain.model.lista.eventos;

import java.time.LocalDateTime;

import es.um.pds.tarjetas.common.events.EventoDominio;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public record LimiteListaConfigurado(
	ListaId listaId,
	TableroId tableroId,
	UsuarioId usuarioId,
	LocalDateTime timestamp,
	Integer limiteAnterior,
	Integer limiteNuevo,
	String nombreLista
) implements EventoDominio {

}
