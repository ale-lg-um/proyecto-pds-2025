package es.um.pds.tarjetas.application.usecases;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import es.um.pds.tarjetas.domain.model.entryHistorial.id.EntryHistorialId;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.EntryHistorial;
import es.um.pds.tarjetas.domain.model.lista.eventos.LimiteListaConfigurado;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaCreada;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaEditada;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaEliminada;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaEspecialDefinida;
import es.um.pds.tarjetas.domain.model.lista.eventos.PrerrequisitosListaConfigurados;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroBloqueado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroCreado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroCreadoDesdePlantilla;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroDesbloqueado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroEditado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroEliminado;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.EtiquetaAnadidaATarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.EtiquetaEliminadaDeTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.EtiquetaModificadaEnTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaCompletada;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaCreada;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaEditada;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaEliminada;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaMovida;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaRenombrada;
import es.um.pds.tarjetas.domain.ports.output.RepositorioEntryHistorial;


@Component
public class ManejadorEventosHistorial {

	private final RepositorioEntryHistorial repoHistorial;

	public ManejadorEventosHistorial(RepositorioEntryHistorial repoHistorial) {
		this.repoHistorial = repoHistorial;
	}

	// ---------- TABLEROS ----------
	@EventListener
	public void manejar(TableroCreado evento) {
		try {
			EntryHistorial entry = EntryHistorial.tableroCreado(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.nombreTablero());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TableroCreado", e);
		}
	}

	@EventListener
	public void manejar(TableroCreadoDesdePlantilla evento) {
		try {
			EntryHistorial entry = EntryHistorial.tableroCreadoDesdePlantilla(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.nombreTablero(), evento.nombrePlantilla());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TableroCreadoDesdePlantilla", e);
		}
	}

	@EventListener
	public void manejar(TableroEditado evento) {
		try {
			EntryHistorial entry = EntryHistorial.tableroEditado(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.nombreAnterior(), evento.nombreNuevo());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TableroEditado", e);
		}
	}

	@EventListener
	public void manejar(TableroEliminado evento) {
		try {
			EntryHistorial entry = EntryHistorial.tableroEliminado(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TableroEliminado", e);
		}
	}

	@EventListener
	public void manejar(TableroBloqueado evento) {
		try {
			EntryHistorial entry = EntryHistorial.tableroBloqueado(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.motivo());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TableroBloqueado", e);
		}
	}

	@EventListener
	public void manejar(TableroDesbloqueado evento) {
		try {
			EntryHistorial entry = EntryHistorial.tableroDesbloqueado(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp());

			repoHistorial.guardar(entry);
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

			repoHistorial.guardar(entry);
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

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para ListaEditada", e);
		}
	}

	@EventListener
	public void manejar(ListaEliminada evento) {
		try {
			EntryHistorial entry = EntryHistorial.listaEliminada(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.listaId(), evento.nombreLista());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para ListaEliminada", e);
		}
	}

	@EventListener
	public void manejar(LimiteListaConfigurado evento) {
		try {
			EntryHistorial entry = EntryHistorial.limiteListaConfigurado(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.listaId(), evento.limiteAnterior(),
					evento.limiteNuevo());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para LimiteListaConfigurado", e);
		}
	}

	@EventListener
	public void manejar(ListaEspecialDefinida evento) {
		try {
			EntryHistorial entry = EntryHistorial.listaEspecialDefinida(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.listaId(), evento.esEspecial());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para ListaEspecialDefinida", e);
		}
	}

	@EventListener
	public void manejar(PrerrequisitosListaConfigurados evento) {
		try {
			EntryHistorial entry = EntryHistorial.prerrequisitosListaConfigurados(EntryHistorialId.of(),
					evento.tableroId(), evento.usuarioId(), evento.timestamp(), evento.listaId(),
					evento.prerrequisitos());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para PrerrequisitosListaConfigurados",
					e);
		}
	}

	// ---------- TARJETAS ----------
	@EventListener
	public void manejar(TarjetaCreada evento) {
		try {
			EntryHistorial entry = EntryHistorial.tarjetaCreada(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.listaId());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TarjetaCreada", e);
		}
	}

	@EventListener
	public void manejar(TarjetaEditada evento) {
		try {
			EntryHistorial entry = EntryHistorial.tarjetaEditada(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.contenidoAnterior(),
					evento.contenidoNuevo());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TarjetaEditada", e);
		}
	}

	@EventListener
	public void manejar(TarjetaRenombrada evento) {
		try {
			EntryHistorial entry = EntryHistorial.tarjetaRenombrada(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.nombreAnterior(),
					evento.nombreNuevo());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TarjetaRenombrada", e);
		}
	}

	@EventListener
	public void manejar(TarjetaEliminada evento) {
		try {
			EntryHistorial entry = EntryHistorial.tarjetaEliminada(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.listaId(), evento.titulo());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TarjetaEliminada", e);
		}
	}

	@EventListener
	public void manejar(TarjetaCompletada evento) {
		try {
			EntryHistorial entry = EntryHistorial.tarjetaCompletada(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.listaId(), evento.completada());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TarjetaCompletada", e);
		}
	}

	@EventListener
	public void manejar(TarjetaMovida evento) {
		try {
			EntryHistorial entry = EntryHistorial.tarjetaMovida(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.listaOrigenId(),
					evento.listaDestinoId());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al registrar entrada del historial para TarjetaMovida", e);
		}
	}

	@EventListener
	public void manejar(EtiquetaAnadidaATarjeta evento) {
		try {
			EntryHistorial entry = EntryHistorial.tarjetaEtiquetada(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.etiqueta());

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al añadir etiqueta a tarjeta", e);
		}
	}

	@EventListener
	public void manejar(EtiquetaEliminadaDeTarjeta evento) {
		try {
			EntryHistorial entry = EntryHistorial.etiquetaEliminadaDeTarjeta(EntryHistorialId.of(), evento.tableroId(),
					evento.usuarioId(), evento.timestamp(), evento.tarjetaId(), evento.etiqueta());

			repoHistorial.guardar(entry);
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

			repoHistorial.guardar(entry);
		} catch (Exception e) {
			throw new RuntimeException("Error al eliminar etiqueta de tarjeta", e);
		}
	}
}