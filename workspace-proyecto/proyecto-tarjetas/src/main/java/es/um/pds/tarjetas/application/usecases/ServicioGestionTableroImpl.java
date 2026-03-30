package es.um.pds.tarjetas.application.usecases;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.application.common.exceptions.PrerrequisitosNoCumplidosException;
import es.um.pds.tarjetas.common.events.EventBus;
import es.um.pds.tarjetas.domain.model.entryHistorial.id.EntryHistorialId;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.EntryHistorial;
import es.um.pds.tarjetas.domain.model.lista.eventos.LimiteListaConfigurado;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaCreada;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaEditada;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaEliminada;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaEspecialDefinida;
import es.um.pds.tarjetas.domain.model.lista.eventos.PrerrequisitosListaConfigurados;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.lista.model.PrerrequisitoInfo;
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
import es.um.pds.tarjetas.domain.rules.PoliticaListas;
import es.um.pds.tarjetas.domain.rules.PoliticaTarjetas;

@Service
public class ServicioGestionTableroImpl implements ServicioGestionTablero {
	// Inyectamos dependencias estrictas (patrón fachada)
	private final RepositorioTableros repoTableros;
	private final RepositorioListas repoListas;
	private final RepositorioTarjetas repoTarjetas;
	private final RepositorioPlantillas repoPlantillas;
	private final EventBus eventBus;
	private final PoliticaListas politicaListas;
	private final PoliticaTarjetas politicaTarjetas;

	// Constructor
	public ServicioGestionTableroImpl(RepositorioTableros repoTableros, RepositorioListas repoListas,
			RepositorioTarjetas repoTarjetas, RepositorioPlantillas repoPlantillas, EventBus eventBus,
			PoliticaListas politicaListas, PoliticaTarjetas politicaTarjetas) {
		this.repoTableros = repoTableros;
		this.repoListas = repoListas;
		this.repoTarjetas = repoTarjetas;
		this.repoPlantillas = repoPlantillas;
		this.eventBus = eventBus;
		this.politicaListas = politicaListas;
		this.politicaTarjetas = politicaTarjetas;
	}

	/**
	 * PASOS A SEGUIR: 1. Validar comando / argumentos 2. Cargar agregados
	 * necesarios 3. Ejecutar operación de dominio 4. Persistir cambios 5. Publicar
	 * eventos 6. Devolver resultado
	 */

	// Métodos heredados de la clase padre

	// TODO Revisar todo este método
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
		UsuarioId idUsuario = UsuarioId.of(emailUsuario);
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

		eventBus.publicar(new TableroBloqueado(idTablero, idUsuario, timestamp, motivo));
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
		UsuarioId idUsuario = UsuarioId.of(emailUsuario);

		// 3. Recuperar agregado
		Tablero tablero = repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));

		// 4. Aplicar lógica de dominio
		tablero.desbloquear();

		// 5. Persistir cambios
		repoTableros.guardar(tablero);

		// 6. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();

		eventBus.publicar(new TableroDesbloqueado(idTablero, idUsuario, timestamp));
	}

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
		UsuarioId idUsuario = UsuarioId.of(emailUsuario);
		ListaId nuevaListaId = ListaId.of();

		// 3. Recuperar raíz del agregado
		Tablero tablero = repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));

		// 4. Aplicar la regla de negocio R11: Nombre único dentro del tablero
		politicaListas.validarNombreUnicoEnTablero(idTablero, nombre);

		// 5. Crear la lista y delegar la lógica al dominio

		// Posición es donde se acaba de meter la lista una vez creada
		// Podríamos más adelante hacer métodos para cambiar la posición de la lista,
		// pero complica UI
		int posicion = tablero.getListas().size() + 1;
		Lista nuevaLista = Lista.of(nuevaListaId, nombre, posicion);
		tablero.anadirLista(nuevaListaId);

		// 6. Persistir cambios
		repoListas.guardar(nuevaLista);
		repoTableros.guardar(tablero);

		// 7. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new ListaCreada(nuevaListaId, idTablero, idUsuario, timestamp, nombre, posicion));

		// 8. Devolver DTO de salida
		return new ListaDTO(nuevaLista);
	}

	@Override
	@Transactional
	public void renombrarLista(String tableroId, String listaId, String nombreNuevo, String emailUsuario) {

		// 1. Validaciones de frontera
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}

		if (listaId == null || listaId.isBlank()) {
			throw new IllegalArgumentException("El identificador de la lista no puede ser null o vacío");
		}

		if (nombreNuevo == null || nombreNuevo.isBlank()) {
			throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
		}

		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede estar vacío");
		}

		// 2. Construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		ListaId idLista = ListaId.of(listaId);
		UsuarioId idUsuario = UsuarioId.of(emailUsuario);
		
		// 3. Recuperar raíz del agregado
		Lista lista = repoListas.buscarPorId(idLista)
				.orElseThrow(() -> new IllegalArgumentException("No existe la lista indicada"));
		
		// 4. Aplicar la regla de negocio R11: Nombre único dentro del tablero
		politicaListas.validarNombreUnicoEnTablero(idTablero, nombreNuevo);
		
		// 5. Renombrar la lista delegando la lógica al dominio
		// Obtener datos necesarios antes del renombrado
		String nombreAnterior = lista.getNombreLista();
		lista.renombrar(nombreNuevo);
		
		// 6. Persistir cambios
		repoListas.guardar(lista);
		
		// 7. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new ListaEditada(idLista, idTablero, idUsuario, timestamp, nombreAnterior, nombreNuevo));
	}

	@Override
	@Transactional
	public void eliminarLista(String tableroId, String listaId, String emailUsuario) {

		// 1. Validaciones de frontera
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}

		if (listaId == null || listaId.isBlank()) {
			throw new IllegalArgumentException("El identificador de la lista no puede ser null o vacío");
		}

		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede estar vacío");
		}

		// 2. Construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		ListaId idLista = ListaId.of(listaId);
		UsuarioId idUsuario = UsuarioId.of(emailUsuario);

		// 3. Recuperar raíces de agregados
		Tablero tablero = repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));

		Lista lista = repoListas.buscarPorId(idLista)
				.orElseThrow(() -> new IllegalArgumentException("No existe la lista indicada"));

		// 4. Eliminar la lista delegando la lógica al dominio
		// Obtener datos necesarios antes del borrado
		String nombreLista = lista.getNombreLista();
		
		tablero.eliminarLista(idLista);

		// 5. Persistir cambios
		repoListas.eliminarPorId(idLista);
		repoTableros.guardar(tablero);

		// 6. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new ListaEliminada(idLista, idTablero, idUsuario, timestamp, nombreLista));
	}

	@Override
	@Transactional
	public void definirListaEspecial(String tableroId, String listaId, String emailUsuario) {
		
		// 1. Validaciones de frontera
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}

		if (listaId == null || listaId.isBlank()) {
			throw new IllegalArgumentException("El identificador de la lista no puede ser null o vacío");
		}

		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede estar vacío");
		}
		
		// 2. Construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		ListaId idLista = ListaId.of(listaId);
		UsuarioId idUsuario = UsuarioId.of(emailUsuario);
	
		// 3. Recuperar raíces de agregados
		Tablero tablero = repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));
		
		Lista lista = repoListas.buscarPorId(idLista)
				.orElseThrow(() -> new IllegalArgumentException("No existe la lista indicada"));
		
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}
		
		// 4. Definir la lista especial delegando la lógica al dominio	
		// El dominio se encarga de ver si ya hay lista especial y si es la misma
		tablero.definirListaEspecial(idLista);
		// Si la lista se hace especial se elimina el límite N
		lista.configurarLimite(null);
		lista.hacerEspecial();

		
		// Obtener datos necesarios para el evento
		String nombreLista = lista.getNombreLista();
		boolean esEspecial = lista.isEspecial();
		
		// 5. Persistir cambios
		repoListas.guardar(lista);
		repoTableros.guardar(tablero);
		
		// 6. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new ListaEspecialDefinida(idLista, idTablero, idUsuario, timestamp, nombreLista, esEspecial));
	}

	// TODO Debería haber un método para configurar límites de todas las listas desde el tablero, no solamente un método individual
	// TODO ¿Hay método para eliminar el límite? También no solo individual, sino a nivel de tablero
	// Si la lista es especial no se puede configurar un límite
	@Override
	@Transactional
	public void configurarLimiteLista(String tableroId, String listaId, Integer limite, String emailUsuario) {

		// 1. Validaciones de frontera
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}

		if (listaId == null || listaId.isBlank()) {
			throw new IllegalArgumentException("El identificador de la lista no puede ser null o vacío");
		}

		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede estar vacío");
		}
		
		// El límite puede ser nulo, se comprueba también si es positivo en el dominio
		
		// 2. Construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		ListaId idLista = ListaId.of(listaId);
		UsuarioId idUsuario = UsuarioId.of(emailUsuario);
		
		// 3. Recuperar raíces de agregados
		Tablero tablero = repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));
		
		Lista lista = repoListas.buscarPorId(idLista)
				.orElseThrow(() -> new IllegalArgumentException("No existe la lista indicada"));
		
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}
		
		// 4. Establecer el límite a la lista delegando la lógica en el dominio
		// Obtener datos necesarios para el evento
		Integer limiteAnterior = lista.getLimite();
		
		lista.configurarLimite(limite);
		
		// 5. Persistir cambios
		repoListas.guardar(lista);
		
		// 6. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new LimiteListaConfigurado(idLista, idTablero, idUsuario, timestamp, limiteAnterior, limite));
	}

	/*
	 * Para los prerrequisitos consideramos que es correcto si la tarjeta ha pasado por las listas
	 * que se indican, pero no se comprueba si ha sido en el orden en el que se indica o si solo ha sido por
	 * esas listas porque complicaría mucho el flujo para el usuario, por eso usamos Set
	 * 
	 * Si se establece un prerrequisito y la lista tiene tarjetas que no cumplan los prerrequisitos
	 * entonces la operación tampoco debería permitirse
	 */
	@Override
	@Transactional
	public void configurarPrerrequisitosLista(String tableroId, String listaId, Set<String> prerrequisitos,
			String emailUsuario) {
		// 1. Validaciones de frontera
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}

		if (listaId == null || listaId.isBlank()) {
			throw new IllegalArgumentException("El identificador de la lista no puede ser null o vacío");
		}

		if (emailUsuario == null || emailUsuario.isBlank()) {
			throw new IllegalArgumentException("El email del usuario no puede estar vacío");
		}

		// Conjunto vacío equivalente a eliminar los prerrequisitos
		if (prerrequisitos == null) {
			throw new IllegalArgumentException("Los prerrequisitos no puedn ser nulos");
		}

		// 2. Construcción de objetos del dominio
		TableroId idTablero = TableroId.of(tableroId);
		ListaId idLista = ListaId.of(listaId);
		UsuarioId idUsuario = UsuarioId.of(emailUsuario);

		// 3. Recuperar raíces de agregados
		Tablero tablero = repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));

		Lista lista = repoListas.buscarPorId(idLista)
				.orElseThrow(() -> new IllegalArgumentException("No existe la lista indicada"));

		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}

		// 4. Aplicar la regla de negocio para validar que todas las listas que se
		// quieran configurar como prerrequisito existan en el tablero (R10)

		// Mapear Strings a ListaId
		Set<ListaId> prerrequisitosIds = prerrequisitos.stream().map(ListaId::of).collect(Collectors.toSet());

		// Validar que todas las listas especificadas en los prerrequisitos existen y son válidas
		politicaListas.validarPrerrequisitosConfigurados(tablero.getListas(), prerrequisitosIds);

		// Extraer del repositorio todas las TarjetaId para guardarlo en una lista de Tarjeta
		List<Tarjeta> listaTarjetas = repoTarjetas.buscarPorListaId(idLista);

		// Usar método validarConfiguracionPrerrequisitos de PoliticaTarjeta para validar que todas las tarjetas
		// que están ya en la lista cumplen los prerrequisitos
		try {
			politicaTarjetas.validarConfiguracionPrerrequisitos(listaTarjetas, prerrequisitosIds);
		} catch (PrerrequisitosNoCumplidosException e) {
			throw new IllegalStateException("No se han podido configurar los prerrequisitos de la lista"
					+ "	porque alguna/s tarjetas de la lista " + lista.getNombreLista()
					+ " no cumplen las condiciones");
		}

		// 5. Configurar los prerrequisitos de la lista delegando la lógica en el dominio
		lista.configurarPrerrequisitos(prerrequisitosIds);

		// Obtener prerrequisitos simplificados para el evento
		Set<PrerrequisitoInfo> prerrequisitosSimplificados = repoListas.buscarPorIds(prerrequisitosIds).stream()
				.map(l -> new PrerrequisitoInfo(l.getIdentificador(), l.getNombreLista())).collect(Collectors.toSet());

		// 6. Persistir cambios
		repoListas.guardar(lista);

		// 7. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new PrerrequisitosListaConfigurados(idLista, idTablero, idUsuario, timestamp,
				prerrequisitosSimplificados));
	}

	/*
	 * NO SE PUEDEN CLONAR TARJETAS, no se puede añadir una tarjeta a una lista que ya tenga esa tarjeta. Tarjetas únicas
	 * Por tanto, no habrá problema con usar List<TarjetaId> en Lista, porque nos aseguramos de que no haya repetidos. Recomprobarlo igualmente
	 */
	
	@Override
	@Transactional
	public TarjetaDTO crearTarjeta(String tableroId, String listaId, TarjetaDTO tarjeta, String emailUsuario) {
		// 1. Validaciones de frontera
		if (tableroId == null || tableroId.isBlank()) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser null o vacío");
		}

		if (listaId == null || listaId.isBlank()) {
			throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
		}
		
		if (tarjeta == null) {
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

		// 3. Recuperar raíz del agregado
		Lista lista = repoListas.buscarPorId(idLista)
				.orElseThrow(() -> new IllegalArgumentException("No existe la lista indicada"));

		// 4. Crear la tarjeta y delegar la lógica al dominio
		int posicion = lista.getListaTarjetas().size() + 1;

		try {
			ContenidoTarjeta contenido = tarjeta.contenido().toDomain();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		

		Tarjeta nuevaTarjeta = Tarjeta.of(nuevaTarjetaId, tarjeta.titulo(), idLista, posicion, contenido);

		lista.anadirTarjeta(nuevaTarjetaId);

		// 5. Persistir cambios
		repoListas.guardar(lista);

		// 6. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new TarjetaCreada(nuevaTarjetaId, idLista, idTablero, usuarioId, timestamp, tarjeta.titulo(),
				posicion));

		// 7. devolver DTO de salida
		return new TarjetaDTO(nuevaTarjeta);
	}

	@Override
	@Transactional
	public void editarTarjeta(String tableroId, String tarjetaId, TarjetaDTO tarjetaActualizada, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public void eliminarTarjeta(String tableroId, String tarjetaId, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public void moverTarjeta(String tableroId, String tarjetaId, String listaDestinoId, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public void completarTarjeta(String tableroId, String tarjetaId, String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public void addEtiquetaATarjeta(String tableroId, String tarjetaId, String nombre, String color,
			String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public void eliminarEtiquetaDeTarjeta(String tableroId, String tarjetaId, String nombre, String color,
			String emailUsuario) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public void modificarEtiquetaEnTarjeta(String tableroId, String tarjetaId, String nombreOld, String colorOld,
			String nombreNuevo, String colorNuevo, String emailUsuario) {
		// TODO Auto-generated method stub

	}
}