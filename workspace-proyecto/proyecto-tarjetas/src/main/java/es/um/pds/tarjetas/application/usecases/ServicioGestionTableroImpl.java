package es.um.pds.tarjetas.application.usecases;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.common.events.EventBus;
import es.um.pds.tarjetas.domain.model.entryHistorial.id.EntryHistorialId;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.EntryHistorial;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaCreada;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.plantilla.model.Plantilla;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroBloqueado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroCreado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroCreadoDesdePlantilla;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroDesbloqueado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroEditado;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.EstadoBloqueo;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaCreada;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ContenidoTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioGestionTablero;
import es.um.pds.tarjetas.domain.ports.input.ServicioHistorial;
import es.um.pds.tarjetas.domain.ports.input.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.ResultadoCrearTableroDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioPlantillas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;
import es.um.pds.tarjetas.domain.rules.PoliticaTarjetas;

// TODO ¿Detalles relevantes para la entry del historial extraerlos aquí? ¿Timestamp dónde se genera? ¿Detalles?

@Service
public class ServicioGestionTableroImpl implements ServicioGestionTablero {
	// Inyectamos dependencias estrictas (patrón fachada)
	private final RepositorioTableros repoTableros;
	private final RepositorioListas repoListas;
	private final RepositorioPlantillas repoPlantillas;
	private final EventBus eventBus;
	private final PoliticaTarjetas politicaTarjetas;

	// Constructor
	public ServicioGestionTableroImpl(RepositorioTableros repoTableros, RepositorioListas repoListas,
			RepositorioPlantillas repoPlantillas, EventBus eventBus, PoliticaTarjetas politica) {
		this.repoTableros = repoTableros;
		this.repoListas = repoListas;
		this.repoPlantillas = repoPlantillas;
		this.eventBus = eventBus;
		this.politicaTarjetas = politica;
	}

	/**
	 * PASOS A SEGUIR: 1. Validar comando / argumentos 2. Cargar agregados
	 * necesarios 3. Ejecutar operación de dominio 4. Persistir cambios 5. Publicar
	 * eventos 6. Devolver resultado
	 */

	// Métodos heredados de la clase padre

	@Override
	@Transactional
	public ResultadoCrearTableroDTO crearTablero(CrearTableroCmd cmd) {
		if (cmd == null) {
			throw new IllegalArgumentException("El comando CrearTableroCmd no puede ser null");
		}

		if (cmd.nombreTablero() == null || cmd.nombreTablero().isBlank()) {
			throw new IllegalArgumentException("El nombre del tablero no puede estar vacío");
		}

		TableroId nuevoId = TableroId.of();
		String tokenUrl = UUID.randomUUID().toString();

		Tablero nuevoTablero = Tablero.of(nuevoId, cmd.nombreTablero(), tokenUrl);

		LocalDateTime timestamp = LocalDateTime.now();
		Plantilla plantilla = null;

		if (cmd.plantillaId() != null) {
			plantilla = repoPlantillas.buscarPorId(cmd.plantillaId())
					.orElseThrow(() -> new IllegalArgumentException("No existe la plantilla indicada"));

			// TODO método auxiliar o ver cómo lo hacemos
			aplicarPlantillaAlTablero(nuevoTablero, plantilla);
		}

		repoTableros.guardar(nuevoTablero);

		if (plantilla != null) {
			eventBus.publicar(new TableroCreadoDesdePlantilla(nuevoId, cmd.usuarioCreador(), timestamp,
					cmd.nombreTablero(), plantilla.getNombre()));
		} else {
			eventBus.publicar(new TableroCreado(nuevoId, cmd.usuarioCreador(), timestamp, cmd.nombreTablero()));
		}

		return new ResultadoCrearTableroDTO(nuevoTablero.getIdentificador().getId(), nuevoTablero.getNombre(),
				nuevoTablero.getTokenUrl());
	}

	@Override
	@Transactional
	public void renombrarTablero(String tableroId, String nombreNuevo, String emailUsuario) {

		// 1. Validaciones básicas de frontera
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}

		if (nombreNuevo == null || nombreNuevo.isBlank()) {
			throw new IllegalArgumentException("El nuevo nombre del tablero no puede estar vacío");
		}

		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede estar vacío");
		}

		// 2. Construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		UsuarioId idUsuario = UsuarioId.of(emailUsuario);

		// 3. Recuperar el agregado
		Tablero tablero = repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));

		// 4. Obtener datos necesarios para el evento antes del cambio
		String nombreAnterior = tablero.getNombre();

		// 5. Ejecutar la operación de lógica de dominio
		tablero.renombrar(nombreNuevo);

		// 6. Persistir cambios
		repoTableros.guardar(tablero);

		// 7. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();

		eventBus.publicar(new TableroEditado(idTablero, idUsuario, timestamp, nombreAnterior, nombreNuevo));
	}

	@Override
	@Transactional
	public void eliminarTablero(String tableroId, String emailUsuario) {

		// 1. Validaciones básicas de frontera
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}

		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede estar vacío");
		}

		// 2. Construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		UsuarioId.of(emailUsuario);

		// 3. Recuperar el agregado para comprobar que existe
		if (repoTableros.buscarPorId(idTablero).isEmpty()) {
			throw new IllegalArgumentException("No existe el tablero indicado");
		}

		// 4. Eliminar el tablero
		repoTableros.eliminarPorId(idTablero);

		// 5. "El historial de tablero desaparece", no se guarda entry del historial al
		// eliminar
	}

	@Override
	@Transactional
	public void bloquearTablero(String tableroId, LocalDateTime desde, LocalDateTime hasta, String motivo,
			String emailUsuario) {

		// 1. Validaciones de frontera
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}

		if (desde == null) {
			throw new IllegalArgumentException("La fecha de inicio del bloqueo no puede ser null");
		}

		if (hasta == null) {
			throw new IllegalArgumentException("La fecha de fin del bloqueo no puede ser null");
		}

		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede estar vacío");
		}

		if (hasta.isBefore(desde)) {
			throw new IllegalArgumentException(
					"La fecha de fin del bloqueo no puede ser anterior a la fecha de inicio");
		}

		// 2. Construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		UsuarioId usuarioId = UsuarioId.of(emailUsuario);
		EstadoBloqueo estadoBloqueo = new EstadoBloqueo(desde, hasta, motivo);

		// 3. Recuperar agregado
		Tablero tablero = repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));

		// 4. Aplicar lógica de dominio
		tablero.bloquear(estadoBloqueo);

		// 5. Persistir cambios
		repoTableros.guardar(tablero);

		// 6. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();

		eventBus.publicar(new TableroBloqueado(idTablero, usuarioId, timestamp, motivo));
	}

	@Override
	@Transactional
	public void desbloquearTablero(String tableroId, String emailUsuario) {

	    // 1. Validaciones de frontera
	    if (tableroId == null || tableroId.isBlank()) {
	        throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
	    }

	    if (emailUsuario == null || emailUsuario.isBlank()) {
	        throw new IllegalArgumentException("El email del usuario no puede estar vacío");
	    }

	    // 2. Construcción de objetos del dominio
	    TableroId idTablero = TableroId.of(tableroId);
	    UsuarioId usuarioId = UsuarioId.of(emailUsuario);

	    // 3. Recuperar agregado
	    Tablero tablero = repoTableros.buscarPorId(idTablero)
	            .orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));

	    // 4. Aplicar lógica de dominio
	    tablero.desbloquear();

	    // 5. Persistir cambios
	    repoTableros.guardar(tablero);

	    // 6. Publicar evento de dominio
	    LocalDateTime timestamp = LocalDateTime.now();

	    eventBus.publicar(new TableroDesbloqueado(
	            idTablero,
	            usuarioId,
	            timestamp
	    ));
	}

	
	// TODO Hacer regla en PoliticaListas para que sea lista con nombre único y integrar la regla de negocio en este método
	@Override
	@Transactional
	public ListaDTO crearLista(String tableroId, String nombre, String emailUsuario) {

		// 1. Validaciones de frontera
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}

		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
		}

		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede estar vacío");
		}

		// 2. Construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		UsuarioId usuarioId = UsuarioId.of(emailUsuario);
		ListaId nuevaListaId = ListaId.of();

		// 3. Recuperar agregado raíz
		Tablero tablero = repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));

		// 4. Crear la lista y delegar la lógica al dominio

		int posicion = tablero.getListas().size() + 1;

		Lista nuevaLista = Lista.of(nuevaListaId, nombre, posicion);

		tablero.anadirLista(nuevaListaId);

		// Posición es donde se acaba de meter la lista

		// 5. Persistir cambios
		repoListas.guardar(nuevaLista);
		repoTableros.guardar(tablero);

		// 6. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();

		eventBus.publicar(new ListaCreada(nuevaListaId, idTablero, usuarioId, timestamp, nombre, posicion));

		// 7. Devolver DTO de salida
		return new ListaDTO(nuevaLista);
	}

	@Override
	public void renombrarLista(String tableroId, String listaId, String nuevoNombre, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void eliminarLista(String tableroId, String listaId, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void definirListaEspecial(String tableroId, String listaId, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configurarLimiteLista(String tableroId, String listaId, int limite, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configurarPrerrequisitosLista(String tableroId, String listaId, List<String> prerrequisitoIds,
			String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public TarjetaDTO crearTarjeta(String tableroId, String listaId, TarjetaDTO tarjeta, String emailUsuario) {
		// TODO Auto-generated method 
		// 1. Validaciones de frontera
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}

		if (listaId == null || listaId.isBlank()) {
			throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
		}
		
		if(tarjeta == null) {
			throw new IllegalArgumentException("La tarjeta no puede ser null");
		}

		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede estar vacío");
		}
		
		// 2. Construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		ListaId idLista = ListaId.of(listaId);
		UsuarioId usuarioId = UsuarioId.of(emailUsuario);
		TarjetaId nuevaTarjetaId = TarjetaId.of();
		
		// 3. Recuperar agregados
		Tablero tablero = repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));
		
		Lista lista = repoListas.buscarPorId(idLista)
				.orElseThrow(() -> new IllegalArgumentException("No existe la lista indicada"));
		
		// 4. Crear la tarjeta y delegar la lógica al dominio
		int posicion = lista.getListaTarjetas().size() + 1;
		
		ContenidoTarjeta contenido = tarjeta.contenido().toDomain();
		
		Tarjeta nuevaTarjeta = Tarjeta.of(nuevaTarjetaId, tarjeta.titulo(), idLista, posicion, contenido);
		
		lista.anadirTarjeta(nuevaTarjetaId);
		
		// 5. Persistir cambios
		repoListas.guardar(lista);
		
		// 6. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new TarjetaCreada(nuevaTarjetaId, idLista, idTablero, usuarioId, timestamp, tarjeta.titulo(), posicion));
		
		// 7. devolver DTO de salida
		return new TarjetaDTO(nuevaTarjeta);
	}

	@Override
	public void editarTarjeta(String tableroId, String tarjetaId, TarjetaDTO tarjetaActualizada, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void eliminarTarjeta(String tableroId, String tarjetaId, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moverTarjeta(String tableroId, String tarjetaId, String listaDestinoId, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void completarTarjeta(String tableroId, String tarjetaId, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addEtiquetaATarjeta(String tableroId, String tarjetaId, String nombre, String color,
			String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void eliminarEtiquetaDeTarjeta(String tableroId, String tarjetaId, String nombre, String color,
			String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modificarEtiquetaEnTarjeta(String tableroId, String tarjetaId, String nombreOld, String colorOld,
			String nombreNuevo, String colorNuevo, String emailUsuario) {
		// TODO Auto-generated method stub

	}
}