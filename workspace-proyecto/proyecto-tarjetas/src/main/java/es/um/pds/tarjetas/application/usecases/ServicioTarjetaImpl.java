package es.um.pds.tarjetas.application.usecases;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import es.um.pds.tarjetas.common.events.EventBus;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.EtiquetaAnadidaATarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.EtiquetaEliminadaDeTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.EtiquetaModificadaEnTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaCompletada;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaCreada;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaEditada;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaEliminada;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaMovida;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaRenombrada;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Checklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ContenidoTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Etiqueta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ItemChecklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarea;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioTarjeta;
import es.um.pds.tarjetas.domain.ports.input.commands.ContenidoTarjetaCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;
import es.um.pds.tarjetas.domain.rules.PoliticaTarjetas;

@Service
public class ServicioTarjetaImpl implements ServicioTarjeta {
	// Inyectamos dependencias estrictas (patrón fachada)
	private final RepositorioTableros repoTableros;
	private final RepositorioListas repoListas;
	private final RepositorioTarjetas repoTarjetas;
	private final EventBus eventBus;
	private final PoliticaTarjetas politicaTarjetas;

	// Constructor
	public ServicioTarjetaImpl(RepositorioTableros repoTableros, RepositorioListas repoListas,
			RepositorioTarjetas repoTarjetas, EventBus eventBus, PoliticaTarjetas politicaTarjetas) {
		this.repoTableros = repoTableros;
		this.repoListas = repoListas;
		this.repoTarjetas = repoTarjetas;
		this.eventBus = eventBus;
		this.politicaTarjetas = politicaTarjetas;
	}
	
	/**
	 * PASOS GENERALES A SEGUIR: 1. Validar comando / argumentos 2. Cargar agregados
	 * necesarios 3. Ejecutar operación de dominio 4. Persistir cambios 5. Publicar
	 * eventos 6. Devolver resultado
	 */
		
	// Métodos auxiliares
	
	private TableroId construirTableroId(String tableroId) {
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}
		return TableroId.of(tableroId);
	}

	private UsuarioId construirUsuarioId(String emailUsuario) {
		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede ser null o vacío");
		}
		return UsuarioId.of(emailUsuario);
	}
	
	private ListaId construirListaId(String listaId) {
		if (listaId == null || listaId.isBlank()) {
			throw new IllegalArgumentException("El identificador de la lista no puede ser null o vacío");
		}
		return ListaId.of(listaId);
	}
	
	private TarjetaId construirTarjetaId(String tarjetaId) {
		if (tarjetaId == null || tarjetaId.isBlank()) {
			throw new IllegalArgumentException("El identificador de la tarjeta no puede ser null o vacío");
		}
		return TarjetaId.of(tarjetaId);
	}

	private Tablero cargarTablero(TableroId idTablero) {
		return repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));
	}
	
	private Lista cargarLista(ListaId idLista) {
		return repoListas.buscarPorId(idLista)
				.orElseThrow(() -> new IllegalArgumentException("No existe la lista indicada"));
	}
	
	private Tarjeta cargarTarjeta(TarjetaId idTarjeta) {
		return repoTarjetas.buscarPorId(idTarjeta)
				.orElseThrow(() -> new IllegalArgumentException("No existe la tarjeta indicada"));
	}
	
	private ContenidoTarjeta construirContenido(ContenidoTarjetaCmd cmd) {
		return switch (cmd.tipoContenido()) {

		case TAREA -> {
			if (cmd.descripcionTarea() == null || cmd.descripcionTarea().isBlank()) {
				throw new IllegalArgumentException("La descripción de la tarea no puede estar vacía");
			}
			yield Tarea.of(cmd.descripcionTarea());
		}

		case CHECKLIST -> {
			if (cmd.itemsChecklist() == null || cmd.itemsChecklist().isEmpty()) {
				throw new IllegalArgumentException("La checklist debe contener al menos un ítem");
			}

			List<ItemChecklist> items = cmd.itemsChecklist().stream().map(texto -> {
				if (texto == null || texto.isBlank()) {
					throw new IllegalArgumentException("Los ítems del checklist no pueden ser vacíos");
				}
				return ItemChecklist.of(texto); // o new ItemChecklist(...)
			}).toList();

			yield Checklist.of(items);
		}
		};
	}
		
	/*
	 * NO SE PUEDEN CLONAR TARJETAS, no se puede añadir una tarjeta a una lista que ya tenga esa tarjeta. Tarjetas únicas
	 * Por tanto, no habrá problema con usar List<TarjetaId> en Lista, porque nos aseguramos de que no haya repetidos. Recomprobarlo igualmente
	 */
	@Override
	@Transactional
	public TarjetaDTO crearTarjeta(String tableroId, String listaId, String nombre, ContenidoTarjetaCmd cmd) {

		// 1. Validaciones de frontera
		if (cmd == null) {
			throw new IllegalArgumentException("El comando del contenido de la tarjeta no puede ser nulo");
		}
		
		// El nombre no puede ser nulo, especialmente pensando de cara al historial. Podría ser nulo si se decidiera
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre de la tarjeta no puede ser null o vacío");
		}

		// 2. Validación y construcción de objetos del dominio
		ContenidoTarjeta contenido = construirContenido(cmd);
		
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		TarjetaId idTarjeta = TarjetaId.of();
		UsuarioId idUsuario = construirUsuarioId(cmd.emailUsuario());
		Tarjeta nuevaTarjeta = Tarjeta.of(idTarjeta, nombre, idLista, contenido);

		// 3. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista lista = cargarLista(idLista);

		// 4. Comprobar consistencia entre agregados
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}

		// 5. Validar reglas de negocio de creación
		politicaTarjetas.validarCreacion(tablero, lista);

		// 7. Actualizar la lista con la nueva tarjeta delegando la lógica en el dominio
		// Se crea en la última posición de la lista
		lista.anadirTarjeta(idTarjeta);

		// 8. Persistir cambios
		repoTarjetas.guardar(nuevaTarjeta);
		repoListas.guardar(lista);

		// 9. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new TarjetaCreada(idTarjeta, idLista, idTablero, idUsuario, timestamp, nombre));

		// 10. Devolver DTO
		return new TarjetaDTO(nuevaTarjeta);
	}
	
	@Override
	@Transactional
	public void editarContenidoTarjeta(String tableroId, String listaId, String tarjetaId, ContenidoTarjetaCmd cmd) {

		// 1. Validaciones de frontera
		if (cmd == null) {
			throw new IllegalArgumentException("El comando del contenido de la tarjeta no puede ser nulo");
		}

		// 2. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		TarjetaId idTarjeta = construirTarjetaId(tarjetaId);
		UsuarioId idUsuario = construirUsuarioId(cmd.emailUsuario());

		ContenidoTarjeta nuevoContenido = construirContenido(cmd);

		// 3. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Tarjeta tarjeta = cargarTarjeta(idTarjeta);

		// 4. Comprobar consistencia entre agregados
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}

		if (!tarjeta.getListaActual().equals(idLista)) {
			throw new IllegalArgumentException("La tarjeta indicada no pertenece a la lista");
		}

		// 5. Obtener datos necesarios para el evento
		ContenidoTarjeta contenidoAntiguo = tarjeta.getContenido();
		
		// 6. Ejecutar la operación delegando la lógica al dominio
		tarjeta.editarContenido(nuevoContenido);

		// 7. Persistir cambios
		repoTarjetas.guardar(tarjeta);

		// 8. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new TarjetaEditada(idTarjeta, idLista, idTablero, idUsuario, timestamp, contenidoAntiguo,
				nuevoContenido));
	}

	@Override
	@Transactional
	public void renombrarTarjeta(String tableroId, String listaId, String tarjetaId, String nuevoNombre,
			String emailUsuario) {

		// 1. Validaciones de frontera
		if (nuevoNombre == null || nuevoNombre.isBlank()) {
			throw new IllegalArgumentException("El nombre de la tarjeta no puede ser null o vacío");
		}

		// 2. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		TarjetaId idTarjeta = construirTarjetaId(tarjetaId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);

		// 3. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Tarjeta tarjeta = cargarTarjeta(idTarjeta);

		// 4. Comprobar consistencia entre agregados
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}

		if (!tarjeta.getListaActual().equals(idLista)) {
			throw new IllegalArgumentException("La tarjeta indicada no pertenece a la lista");
		}

		// 5. Obtener datos necesarios para el evento
		String nombreAnterior = tarjeta.getTitulo();
		
		// 6. Ejecutar la operación delegando la lógica al dominio
		tarjeta.cambiarTitulo(nuevoNombre);

		// 7. Persistir cambios
		repoTarjetas.guardar(tarjeta);

		// 8. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new TarjetaRenombrada(idTarjeta, idLista, idTablero, idUsuario, timestamp, nombreAnterior,
				nuevoNombre));
	}

	@Override
	@Transactional
	public void eliminarTarjeta(String tableroId, String listaId, String tarjetaId, String emailUsuario) {

		// 1. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		TarjetaId idTarjeta = construirTarjetaId(tarjetaId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);

		// 2. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista lista = cargarLista(idLista);
		Tarjeta tarjeta = cargarTarjeta(idTarjeta);

		// 3. Comprobar consistencia entre agregados
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}

		if (!tarjeta.getListaActual().equals(idLista)) {
			throw new IllegalArgumentException("La tarjeta indicada no pertenece a la lista");
		}

		if (!lista.getListaTarjetas().contains(idTarjeta)) {
			throw new IllegalArgumentException("La tarjeta indicada no está contenida en la lista");
		}

		// 4. Obtener datos necesarios para el evento antes del borrado
		String nombreTarjeta = tarjeta.getTitulo();

		// 5. Ejecutar la operación delegando la lógica al dominio
		lista.eliminarTarjeta(idTarjeta);

		// 6. Persistir cambios
		repoListas.guardar(lista);
		repoTarjetas.eliminarPorId(idTarjeta);

		// 7. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new TarjetaEliminada(idTarjeta, idLista, idTablero, idUsuario, timestamp, nombreTarjeta));
	}

	@Override
	@Transactional
	public void moverTarjeta(String tableroId, String tarjetaId, String listaOrigenId, String listaDestinoId,
			String emailUsuario) {

		// 1. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		TarjetaId idTarjeta = construirTarjetaId(tarjetaId);
		ListaId idListaOrigen = construirListaId(listaOrigenId);
		ListaId idListaDestino = construirListaId(listaDestinoId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);

		// 2. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista listaOrigen = cargarLista(idListaOrigen);
		Lista listaDestino = cargarLista(idListaDestino);
		Tarjeta tarjeta = cargarTarjeta(idTarjeta);

		// 3. Comprobar consistencia entre agregados
		if (!tablero.getListas().contains(idListaOrigen)) {
			throw new IllegalArgumentException("La lista origen no pertenece al tablero");
		}

		if (!tablero.getListas().contains(idListaDestino)) {
			throw new IllegalArgumentException("La lista destino no pertenece al tablero");
		}

		if (!tarjeta.getListaActual().equals(idListaOrigen)) {
			throw new IllegalArgumentException("La tarjeta indicada no pertenece a la lista origen");
		}

		if (!listaOrigen.getListaTarjetas().contains(idTarjeta)) {
			throw new IllegalArgumentException("La tarjeta indicada no está contenida en la lista origen");
		}

		// 4. Validar reglas de negocio del movimiento
		politicaTarjetas.validarMovimientoEntreListas(tarjeta, listaDestino);

		// 5. Obtener datos necesarios para el evento
		String nombreTarjeta = tarjeta.getTitulo();
		
		// 6. Ejecutar la operación delegando la lógica al dominio
		listaOrigen.eliminarTarjeta(idTarjeta);
		listaDestino.anadirTarjeta(idTarjeta);
		tarjeta.cambiarListaActual(idListaDestino);

		// 7. Persistir cambios
		repoListas.guardar(listaOrigen);
		repoListas.guardar(listaDestino);
		repoTarjetas.guardar(tarjeta);

		// 8. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new TarjetaMovida(idTarjeta, idListaOrigen, idListaDestino, idTablero, idUsuario, timestamp, nombreTarjeta));
	}

	// Realmente es mover una tarjeta a la lista especial de completadas y se podría prescindir,
	// pero lo separamos como acciones distintas por tener más coherencia con el dominio
	@Override
	@Transactional
	public void completarTarjeta(String tableroId, String listaId, String tarjetaId, String emailUsuario) {

		// 1. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idListaOrigen = construirListaId(listaId);
		TarjetaId idTarjeta = construirTarjetaId(tarjetaId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);

		// 2. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista listaOrigen = cargarLista(idListaOrigen);
		Tarjeta tarjeta = cargarTarjeta(idTarjeta);

		// 3. Comprobar consistencia entre agregados
		if (!tablero.getListas().contains(idListaOrigen)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}

		if (!tarjeta.getListaActual().equals(idListaOrigen)) {
			throw new IllegalArgumentException("La tarjeta indicada no pertenece a la lista indicada");
		}

		if (!listaOrigen.getListaTarjetas().contains(idTarjeta)) {
			throw new IllegalArgumentException("La tarjeta indicada no está contenida en la lista indicada");
		}

		// 4. Obtener la lista especial desde el tablero
		ListaId idListaEspecial = tablero.getListaEspecial();
		if (idListaEspecial == null) {
			throw new IllegalStateException("El tablero no tiene una lista especial configurada");
		}

		if (!tablero.getListas().contains(idListaEspecial)) {
			throw new IllegalStateException("La lista especial configurada no pertenece al tablero");
		}

		Lista listaEspecial = repoListas.buscarPorId(idListaEspecial)
				.orElseThrow(() -> new IllegalArgumentException("No existe la lista especial indicada en el tablero"));

		// 5. Validar reglas de negocio de completar tarjeta
		politicaTarjetas.validarCompletar(listaEspecial, tarjeta);

		// 6. Obtener datos necesarios para el evento
		String nombreTarjeta = tarjeta.getTitulo();

		// 7. Ejecutar la operación delegando la lógica al dominio
		listaOrigen.eliminarTarjeta(idTarjeta);
		listaEspecial.anadirTarjeta(idTarjeta);
		tarjeta.cambiarListaActual(idListaEspecial);

		// 8. Persistir cambios
		repoListas.guardar(listaOrigen);
		repoListas.guardar(listaEspecial);
		repoTarjetas.guardar(tarjeta);

		// 9. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(
				new TarjetaCompletada(idTarjeta, idListaEspecial, idTablero, idUsuario, timestamp, nombreTarjeta));
	}

	@Override
	@Transactional
	public void addEtiquetaATarjeta(String tableroId, String listaId, String tarjetaId, String nombre, String color,
			String emailUsuario) {

		// 1. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		TarjetaId idTarjeta = construirTarjetaId(tarjetaId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);
		
		Etiqueta nuevaEtiqueta = new Etiqueta(nombre, color);

		// 2. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista lista = cargarLista(idLista);
		Tarjeta tarjeta = cargarTarjeta(idTarjeta);

		// 3. Comprobar consistencia entre agregados
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}

		if (!tarjeta.getListaActual().equals(idLista)) {
			throw new IllegalArgumentException("La tarjeta indicada no pertenece a la lista");
		}

		if (!lista.getListaTarjetas().contains(idTarjeta)) {
			throw new IllegalArgumentException("La tarjeta indicada no está contenida en la lista");
		}

		// 4. Obtener datos necesarios para el evento
		String nombreTarjeta = tarjeta.getTitulo();

		// 5. Aplicar la operación delegando la lógica al dominio
		tarjeta.anadirEtiqueta(nuevaEtiqueta);

		// 6. Persistir cambios
		repoTarjetas.guardar(tarjeta);

		// 7. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(
				new EtiquetaAnadidaATarjeta(idTarjeta, idLista, idTablero, idUsuario, timestamp, nuevaEtiqueta, nombreTarjeta));
	}

	@Override
	@Transactional
	public void eliminarEtiquetaDeTarjeta(String tableroId, String listaId, String tarjetaId, String nombre,
			String color, String emailUsuario) {

		// 1. Validación y construcción de objetos del dominio	
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		TarjetaId idTarjeta = construirTarjetaId(tarjetaId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);
		
		Etiqueta etiquetaAEliminar = new Etiqueta(nombre, color);

		// 2. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista lista = cargarLista(idLista);
		Tarjeta tarjeta = cargarTarjeta(idTarjeta);

		// 3. Comprobar consistencia entre agregados
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}

		if (!tarjeta.getListaActual().equals(idLista)) {
			throw new IllegalArgumentException("La tarjeta indicada no pertenece a la lista");
		}

		if (!lista.getListaTarjetas().contains(idTarjeta)) {
			throw new IllegalArgumentException("La tarjeta indicada no está contenida en la lista");
		}

		// 4. Obtener datos necesarios para el evento
		String nombreTarjeta = tarjeta.getTitulo();

		// 5. Aplicar la operación delegando la lógica al dominio
		tarjeta.eliminarEtiqueta(etiquetaAEliminar);

		// 6. Persistir cambios
		repoTarjetas.guardar(tarjeta);

		// 7. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new EtiquetaEliminadaDeTarjeta(idTarjeta, idLista, idTablero, idUsuario, timestamp,
				etiquetaAEliminar, nombreTarjeta));
	}

	@Override
	@Transactional
	public void modificarEtiquetaEnTarjeta(String tableroId, String listaId, String tarjetaId, String nombreOld,
			String colorOld, String nombreNuevo, String colorNuevo, String emailUsuario) {
		
		// 1. Validación y construcción de objetos del dominio	
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		TarjetaId idTarjeta = construirTarjetaId(tarjetaId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);
		
		Etiqueta etiquetaEliminada = new Etiqueta(nombreOld, colorOld);
		Etiqueta etiquetaNueva = new Etiqueta(nombreNuevo, colorNuevo);

		// 2. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista lista = cargarLista(idLista);
		Tarjeta tarjeta = cargarTarjeta(idTarjeta);

		// 3. Comprobar consistencia entre agregados
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}

		if (!tarjeta.getListaActual().equals(idLista)) {
			throw new IllegalArgumentException("La tarjeta indicada no pertenece a la lista");
		}

		if (!lista.getListaTarjetas().contains(idTarjeta)) {
			throw new IllegalArgumentException("La tarjeta indicada no está contenida en la lista");
		}

		// 4. Obtener datos necesarios para el evento
		String nombreTarjeta = tarjeta.getTitulo();

		// 5. Aplicar la operación delegando la lógica al dominio
		tarjeta.eliminarEtiqueta(etiquetaEliminada);
		tarjeta.anadirEtiqueta(etiquetaNueva);

		// 6. Persistir cambios
		repoTarjetas.guardar(tarjeta);

		// 7. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new EtiquetaModificadaEnTarjeta(idTarjeta, idLista, idTablero, idUsuario, timestamp,
				etiquetaEliminada, etiquetaNueva, nombreTarjeta));

	}
}
