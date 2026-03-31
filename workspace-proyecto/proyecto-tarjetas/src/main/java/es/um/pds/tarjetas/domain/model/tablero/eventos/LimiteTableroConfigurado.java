package es.um.pds.tarjetas.domain.model.tablero.eventos;

import java.time.LocalDateTime;

import es.um.pds.tarjetas.common.events.EventoDominio;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public record LimiteTableroConfigurado(TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, Integer limite) implements EventoDominio {

}
