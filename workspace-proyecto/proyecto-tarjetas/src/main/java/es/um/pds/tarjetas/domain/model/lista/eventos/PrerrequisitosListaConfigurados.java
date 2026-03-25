package es.um.pds.tarjetas.domain.model.lista.eventos;

import java.time.LocalDateTime;
import java.util.Set;

import es.um.pds.tarjetas.common.events.EventoDominio;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public record PrerrequisitosListaConfigurados(
	ListaId listaId,
	TableroId tableroId,
	UsuarioId usuarioId,
	LocalDateTime timestamp,
	Set<ListaId> prerrequisitos
) implements EventoDominio {

}
