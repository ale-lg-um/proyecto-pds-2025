package es.um.pds.tarjetas.application.usecases;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import es.um.pds.tarjetas.domain.model.entryHistorial.id.EntryHistorialId;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.EntryHistorial;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaCreada;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaEditada;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroBloqueado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroCreado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroDesbloqueado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroEditado;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.EtiquetaAnadidaATarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.EtiquetaEliminadaDeTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.EtiquetaModificadaEnTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaCreada;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaMovida;
import es.um.pds.tarjetas.domain.ports.input.ServicioHistorial;

//TODO COMPLETAR CON LOS EVENTOS QUE FALTAN DE ENTRY HISTORIAL

@Component
public class ManejadorEventosHistorial {

	private final ServicioHistorial servicioHistorial;

	public ManejadorEventosHistorial(ServicioHistorial servicioHistorial) {
		this.servicioHistorial = servicioHistorial;
	}

	// ---------- TABLEROS ----------
	@EventListener
	public void manejar(TableroCreado evento) {
		try {
			EntryHistorial entry = EntryHistorial.tableroCreado(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp());

			servicioHistorial.append(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TableroCreado", e);
		}
	}

	@EventListener
	public void manejar(TableroEditado evento) {
		try {
			EntryHistorial entry = EntryHistorial.tableroEditado(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.nombreAnterior(), evento.nombreNuevo());

			servicioHistorial.append(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TableroEditado", e);
		}
	}

	@EventListener
	public void manejar(TableroBloqueado evento) {
		try {
			EntryHistorial entry = EntryHistorial.tableroBloqueado(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.motivo());

			servicioHistorial.append(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TableroBloqueado", e);
		}
	}

	@EventListener
	public void manejar(TableroDesbloqueado evento) {
		try {
			EntryHistorial entry = EntryHistorial.tableroDesbloqueado(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp());

			servicioHistorial.append(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TableroDesbloqueado", e);
		}
	}

	// ---------- LISTAS ----------
	@EventListener
	public void manejar(ListaCreada evento) {
		try {
			EntryHistorial entry = EntryHistorial.listaCreada(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.listaId(), evento.nombreLista());

			servicioHistorial.append(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para ListaCreada", e);
		}
	}

	@EventListener
	public void manejar(ListaEditada evento) {
		try {
			EntryHistorial entry = EntryHistorial.listaEditada(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.listaId(), evento.nombreAnterior(),
					evento.nombreNuevo());

			servicioHistorial.append(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para ListaEditada", e);
		}
	}

	// ---------- TARJETAS ----------
	@EventListener
	public void manejar(TarjetaCreada evento) {
		try {
			EntryHistorial entry = EntryHistorial.tarjetaCreada(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.listaId());

			servicioHistorial.append(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TarjetaCreada", e);
		}
	}

	@EventListener
	public void manejar(TarjetaMovida evento) {
		try {
			EntryHistorial entry = EntryHistorial.tarjetaMovida(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.listaOrigenId(),
					evento.listaDestinoId());

			servicioHistorial.append(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TarjetaMovida", e);
		}
	}

	@EventListener
	public void manejar(EtiquetaAnadidaATarjeta evento) {
		try {
			EntryHistorial entry = EntryHistorial.tarjetaEtiquetada(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.etiqueta());

			servicioHistorial.append(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al añadir etiqueta a tarjeta", e);
		}
	}

	@EventListener
	public void manejar(EtiquetaEliminadaDeTarjeta evento) {
		try {
			EntryHistorial entry = EntryHistorial.etiquetaEliminadaDeTarjeta(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.etiqueta());

			servicioHistorial.append(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al eliminar etiqueta de tarjeta", e);
		}
	}

	@EventListener
	public void manejar(EtiquetaModificadaEnTarjeta evento) {
		try {
			EntryHistorial entry = EntryHistorial.etiquetaModificadaEnTarjeta(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.etiquetaAnterior(),
					evento.etiquetaNueva());

			servicioHistorial.append(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al eliminar etiqueta de tarjeta", e);
		}
	}
}