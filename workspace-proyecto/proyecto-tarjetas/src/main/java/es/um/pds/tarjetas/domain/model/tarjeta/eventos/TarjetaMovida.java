package es.um.pds.tarjetas.domain.model.tarjeta.eventos;

import java.time.LocalDateTime;

import es.um.pds.tarjetas.common.events.EventoDominio;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public record TarjetaMovida(
	TarjetaId tarjetaId,
	ListaId listaOrigenId,
	ListaId listaDestinoId,
	TableroId tableroId,
	UsuarioId usuarioId,
	LocalDateTime timestamp
) implements EventoDominio {

}
