package es.um.pds.tarjetas.domain.model.tarjeta.eventos;

import java.time.LocalDateTime;

import es.um.pds.tarjetas.common.events.EventoDominio;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ContenidoTarjeta;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public record TarjetaEditada(
	TarjetaId tarjetaId,
	ListaId listaId,
	TableroId tableroId,
	UsuarioId usuarioId,
	LocalDateTime timestamp,
	String tituloAnterior,
	String tituloNuevo,
	ContenidoTarjeta contenidoAnterior,
	ContenidoTarjeta contenidoNuevo
) implements EventoDominio {

}
