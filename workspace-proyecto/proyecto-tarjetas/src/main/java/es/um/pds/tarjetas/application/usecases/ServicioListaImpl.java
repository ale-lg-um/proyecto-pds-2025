package es.um.pds.tarjetas.application.usecases;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import es.um.pds.tarjetas.common.events.EventBus;
import es.um.pds.tarjetas.common.exceptions.PrerrequisitosNoCumplidosException;
import es.um.pds.tarjetas.domain.model.lista.eventos.LimiteListaConfigurado;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaCreada;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaEditada;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaEliminada;
import es.um.pds.tarjetas.domain.model.lista.eventos.ListaEspecialDefinida;
import es.um.pds.tarjetas.domain.model.lista.eventos.PrerrequisitosListaConfigurados;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.lista.model.PrerrequisitoInfo;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioLista;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;
import es.um.pds.tarjetas.domain.rules.PoliticaListas;
import es.um.pds.tarjetas.domain.rules.PoliticaTarjetas;

@Service
public class ServicioListaImpl implements ServicioLista {
	// Inyectamos dependencias estrictas (patrón fachada)
	private final RepositorioTableros repoTableros;
	private final RepositorioListas repoListas;
	private final RepositorioTarjetas repoTarjetas;
	private final EventBus eventBus;
	private final PoliticaListas politicaListas;
	private final PoliticaTarjetas politicaTarjetas;

	// Constructor
	public ServicioListaImpl(RepositorioTableros repoTableros, RepositorioListas repoListas,
			RepositorioTarjetas repoTarjetas, EventBus eventBus,
			PoliticaListas politicaListas, PoliticaTarjetas politicaTarjetas) {
		this.repoTableros = repoTableros;
		this.repoListas = repoListas;
		this.repoTarjetas = repoTarjetas;
		this.eventBus = eventBus;
		this.politicaListas = politicaListas;
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

	private Tablero cargarTablero(TableroId idTablero) {
		return repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));
	}
	
	private Lista cargarLista(ListaId idLista) {
		return repoListas.buscarPorId(idLista)
				.orElseThrow(() -> new IllegalArgumentException("No existe la lista indicada"));
	}
	
	@Override
	@Transactional
	public ListaDTO crearLista(String tableroId, String nombre, String emailUsuario) {

		// 1. Validaciones de frontera
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
		}

		// 2. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);
		ListaId nuevaListaId = ListaId.of();

		// 3. Recuperar raíz del agregado
		Tablero tablero = cargarTablero(idTablero);

		// 4. Aplicar la regla de negocio R11: Nombre único dentro del tablero
		politicaListas.validarNombreUnicoEnTablero(idTablero, nombre);

		// 5. Ejecutar la operación delegando la lógica al dominio

		// Podríamos considerar posición de la Lista pero no tendriá mucho sentido de cara a la UI

		Lista nuevaLista = Lista.of(nuevaListaId, nombre);
		nuevaLista.asignarATablero(idTablero);
		tablero.anadirLista(nuevaListaId);

		// 6. Persistir cambios
		repoListas.guardar(nuevaLista);
		repoTableros.guardar(tablero);

		// 7. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new ListaCreada(nuevaListaId, idTablero, idUsuario, timestamp, nombre));

		// 8. Devolver DTO de salida
		return new ListaDTO(nuevaLista);
	}

	@Override
	@Transactional
	public void renombrarLista(String tableroId, String listaId, String nombreNuevo, String emailUsuario) {

		// 1. Validaciones de frontera
		if (nombreNuevo == null || nombreNuevo.isBlank()) {
			throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
		}

		// 2. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);

		// 3. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista lista = cargarLista(idLista);

		// 4. Comprobar consistencia entre agregados
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}

		// 5. Aplicar regla de negocio: nombre único dentro del tablero
		politicaListas.validarNombreUnicoEnTablero(idTablero, nombreNuevo);

		// 6. Renombrar la lista delegando la lógica al dominio
		String nombreAnterior = lista.getNombreLista();
		lista.renombrar(nombreNuevo);

		// 7. Persistir cambios
		repoListas.guardar(lista);

		// 8. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new ListaEditada(idLista, idTablero, idUsuario, timestamp, nombreAnterior, nombreNuevo));
	}

	@Override
	@Transactional
	public void eliminarLista(String tableroId, String listaId, String emailUsuario) {

		// 1. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);

		// 2. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista lista = cargarLista(idLista);

		// 3. Comprobar consistencia entre agregados
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}

		// 4. Obtener datos necesarios para el evento
		String nombreLista = lista.getNombreLista();

		// 5. Borrar todas las tarjetas de la lista y persistir cambios
		repoTarjetas.eliminarPorListaId(idLista);

		// 6. Ejecutar la operación delegando la lógica al dominio
		tablero.eliminarLista(idLista);

		// 7. Persistir cambios
		repoListas.eliminarPorId(idLista);
		repoTableros.guardar(tablero);

		// 8. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new ListaEliminada(idLista, idTablero, idUsuario, timestamp, nombreLista));
	}

	@Override
	@Transactional
	public void definirListaEspecial(String tableroId, String listaId, String emailUsuario) {
		
		// 1. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);
	
		// 2. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista lista = cargarLista(idLista);
		
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}
		
		// 3. Ejecutar la operación delegando la lógica al dominio
		// El dominio se encarga de ver si ya hay lista especial y si es la misma
		tablero.definirListaEspecial(idLista);
		// Si la lista se hace especial se elimina el límite N
		lista.configurarLimite(null);
		lista.hacerEspecial();

		// 4. Obtener datos necesarios para el evento
		String nombreLista = lista.getNombreLista();
		boolean esEspecial = lista.isEspecial();
		
		// 5. Persistir cambios
		repoListas.guardar(lista);
		repoTableros.guardar(tablero);
		
		// 6. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new ListaEspecialDefinida(idLista, idTablero, idUsuario, timestamp, nombreLista, esEspecial));
	}

	// Si la lista es especial no se puede configurar un límite
	// El límite puede ser nulo, se comprueba también si es positivo en el dominio
	@Override
	@Transactional
	public void configurarLimiteLista(String tableroId, String listaId, Integer limite, String emailUsuario) {
		
		// 1. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);
		
		// 2. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista lista = cargarLista(idLista);
		
		if (!tablero.getListas().contains(idLista)) {
			throw new IllegalArgumentException("La lista indicada no pertenece al tablero");
		}
		
		
		// 3. Obtener datos necesarios para el evento
		Integer limiteAnterior = lista.getLimite();
		String nombreLista = lista.getNombreLista();
		
		// 4. Ejecutar la operación delegando la lógica al dominio
		lista.configurarLimite(limite);
		
		// 5. Persistir cambios
		repoListas.guardar(lista);
		
		// 6. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new LimiteListaConfigurado(idLista, idTablero, idUsuario, timestamp, limiteAnterior, limite, nombreLista));
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
		// Conjunto vacío equivalente a eliminar los prerrequisitos
		if (prerrequisitos == null) {
			throw new IllegalArgumentException("Los prerrequisitos no pueden ser nulos");
		}

		// 2. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		ListaId idLista = construirListaId(listaId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);

		// 3. Recuperar raíces de agregados
		Tablero tablero = cargarTablero(idTablero);
		Lista lista = cargarLista(idLista);

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

		// 6. Obtener prerrequisitos simplificados para el evento
		Set<PrerrequisitoInfo> prerrequisitosSimplificados = repoListas.buscarPorIds(prerrequisitosIds).stream()
				.map(l -> new PrerrequisitoInfo(l.getIdentificador(), l.getNombreLista())).collect(Collectors.toSet());
		
		// 7. Obtener datos necesarios para el evento
		String nombreLista = lista.getNombreLista();

		// 8. Persistir cambios
		repoListas.guardar(lista);

		// 9. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new PrerrequisitosListaConfigurados(idLista, idTablero, idUsuario, timestamp,
				prerrequisitosSimplificados, nombreLista));
	}
}
