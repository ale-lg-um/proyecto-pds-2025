package es.um.pds.tarjetas.application.usecases;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.common.events.EventBus;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.plantilla.id.PlantillaId;
import es.um.pds.tarjetas.domain.model.plantilla.model.Plantilla;
import es.um.pds.tarjetas.domain.model.tablero.eventos.LimiteTableroConfigurado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroBloqueado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroCreado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroCreadoDesdePlantilla;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroDesbloqueado;
import es.um.pds.tarjetas.domain.model.tablero.eventos.TableroEditado;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.EstadoBloqueo;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioTablero;
import es.um.pds.tarjetas.domain.ports.input.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.ResultadoCrearTableroDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioPlantillas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;

@Service
public class ServicioTableroImpl implements ServicioTablero {
	// Inyectamos dependencias estrictas (patrón fachada)
	private final RepositorioTableros repoTableros;
	private final RepositorioListas repoListas;
	private final RepositorioTarjetas repoTarjetas;
	private final RepositorioPlantillas repoPlantillas;
	private final EventBus eventBus;

	// Constructor
	public ServicioTableroImpl(RepositorioTableros repoTableros, RepositorioListas repoListas,
			RepositorioTarjetas repoTarjetas, RepositorioPlantillas repoPlantillas, EventBus eventBus) {
		this.repoTableros = repoTableros;
		this.repoListas = repoListas;
		this.repoTarjetas = repoTarjetas;
		this.repoPlantillas = repoPlantillas;
		this.eventBus = eventBus;
	}

	/**
	 * PASOS GENERALES A SEGUIR: 1. Validar comando / argumentos 2. Cargar agregados
	 * necesarios 3. Ejecutar operación de dominio 4. Persistir cambios 5. Publicar
	 * eventos 6. Devolver resultado
	 */

	// Métodos auxiliares

	private void aplicarPlantillaAlTablero(Tablero nuevoTablero, Plantilla plantilla) {
		// TODO
		// Creado para evitar error, pero hay que implementar
	}
	
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

	private Tablero cargarTablero(TableroId idTablero) {
		return repoTableros.buscarPorId(idTablero)
				.orElseThrow(() -> new IllegalArgumentException("No existe el tablero indicado"));
	}
	
	// Métodos heredados de la clase padre

	@Override
	@Transactional
	public ResultadoCrearTableroDTO crearTablero(CrearTableroCmd cmd) {

		// 1. Validaciones de frontera
		if (cmd == null) {
			throw new IllegalArgumentException("El comando CrearTableroCmd no puede ser nulo");
		}

		if (cmd.nombreTablero() == null || cmd.nombreTablero().isBlank()) {
			throw new IllegalArgumentException("El nombre del tablero no puede estar vacío");
		}

		if (cmd.usuarioCreador() == null || cmd.usuarioCreador().isBlank()) {
			throw new IllegalArgumentException("El usuario creador no puede ser nulo o vacío");
		}

		// 2. Validación y construcción de objetos del dominio
		TableroId idTablero = TableroId.of();
		UsuarioId idUsuario = UsuarioId.of(cmd.usuarioCreador());
		String tokenUrl = UUID.randomUUID().toString();

		// 3. Crear agregado raíz
		Tablero nuevoTablero = Tablero.of(idTablero, cmd.nombreTablero(), tokenUrl, idUsuario);

		Plantilla plantilla = null;

		// 4. Cargar plantilla si procede y aplicarla
		if (cmd.plantillaId() != null && !cmd.plantillaId().isBlank()) {
			PlantillaId idPlantilla = PlantillaId.of(cmd.plantillaId());
			plantilla = repoPlantillas.buscarPorId(idPlantilla)
					.orElseThrow(() -> new IllegalArgumentException("No existe la plantilla indicada"));

			aplicarPlantillaAlTablero(nuevoTablero, plantilla);
		}

		// 5. Persistir cambios
		repoTableros.guardar(nuevoTablero);

		// 6. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();

		if (plantilla != null) {
			eventBus.publicar(new TableroCreadoDesdePlantilla(idTablero, idUsuario, timestamp, nuevoTablero.getNombre(),
					plantilla.getNombre()));
		} else {
			eventBus.publicar(new TableroCreado(idTablero, idUsuario, timestamp, nuevoTablero.getNombre()));
		}

		// 7. Devolver DTO de salida
		return new ResultadoCrearTableroDTO(nuevoTablero.getIdentificador().getId(), nuevoTablero.getNombre(),
				nuevoTablero.getTokenUrl());
	}

	@Override
	@Transactional
	public void renombrarTablero(String tableroId, String nombreNuevo, String emailUsuario) {

		// 1. Validaciones básicas de frontera
		if (nombreNuevo == null || nombreNuevo.isBlank()) {
			throw new IllegalArgumentException("El nuevo nombre del tablero no puede estar vacío");
		}

		// 2. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);

		// 3. Recuperar raíz del agregado
		Tablero tablero = cargarTablero(idTablero);

		// 4. Obtener datos necesarios para el evento antes del cambio
		String nombreAnterior = tablero.getNombre();

		// 5. Ejecutar la operación delegando la lógica al dominio
		tablero.renombrar(nombreNuevo);

		// 6. Persistir cambios
		repoTableros.guardar(tablero);

		// 7. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();

		eventBus.publicar(new TableroEditado(idTablero, idUsuario, timestamp, nombreAnterior, nombreNuevo));
	}

	@Override
	@Transactional
	public void eliminarTablero(String tableroId) {

		// 1. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);

		// 2. Recuperar la raíz del agregado
		Tablero tablero = cargarTablero(idTablero);

		// 3. Eliminar todas las tarjetas de las listas del tablero
		Set<Lista> listasTablero = repoListas.buscarPorIds(tablero.getListas());
		for (Lista lista : listasTablero) {
			repoTarjetas.eliminarPorListaId(lista.getIdentificador());
		}

		// 4. Eliminar todas las listas del tablero
		repoListas.eliminarPorTableroId(idTablero);

		// 5. Eliminar el tablero
		repoTableros.eliminarPorId(idTablero);

		// No se genera historial al eliminar el tablero
	}

	@Override
	@Transactional
	public void bloquearTablero(String tableroId, LocalDateTime desde, LocalDateTime hasta, String motivo,
			String emailUsuario) {

		// 1. Validaciones de frontera
		if (desde == null) {
			throw new IllegalArgumentException("La fecha de inicio del bloqueo no puede ser null");
		}

		if (hasta == null) {
			throw new IllegalArgumentException("La fecha de fin del bloqueo no puede ser null");
		}

		if (hasta.isBefore(desde)) {
			throw new IllegalArgumentException(
					"La fecha de fin del bloqueo no puede ser anterior a la fecha de inicio");
		}

		// 2. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);
		EstadoBloqueo estadoBloqueo = new EstadoBloqueo(desde, hasta, motivo);

		// 3. Recuperar agregado
		Tablero tablero = cargarTablero(idTablero);

		// 4. Ejecutar la operación delegando la lógica al dominio
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

		// 1. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);

		// 2. Recuperar agregado
		Tablero tablero = cargarTablero(idTablero);

		// 3. Ejecutar la operación delegando la lógica al dominio
		tablero.desbloquear();

		// 4. Persistir cambios
		repoTableros.guardar(tablero);

		// 5. Publicar evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();

		eventBus.publicar(new TableroDesbloqueado(idTablero, idUsuario, timestamp));
	}
	
	@Override
	@Transactional
	public void configurarLimiteTablero(String tableroId, Integer limite, String emailUsuario) {

		// 1. Validaciones de frontera
		// null significa quitar el límite global
		if (limite != null && limite <= 0) {
			throw new IllegalArgumentException("El límite debe ser un entero positivo");
		}

		// 2. Validación y construcción de objetos del dominio
		TableroId idTablero = construirTableroId(tableroId);
		UsuarioId idUsuario = construirUsuarioId(emailUsuario);

		// 3. Recuperar agregado raíz
		Tablero tablero = cargarTablero(idTablero);

		// 4. Recuperar todas las listas del tablero
		Set<Lista> listasTablero = repoListas.buscarPorIds(tablero.getListas());

		// 5. Validar que ninguna lista no especial supere el límite
		if (limite != null) {
			for (Lista lista : listasTablero) {
				if (!lista.isEspecial() && lista.getListaTarjetas().size() > limite) {
					throw new IllegalStateException("No se puede establecer el límite global porque la lista "
							+ lista.getNombreLista() + " ya contiene más tarjetas que el límite indicado");
				}
			}
		}

		// 6. Aplicar el límite a todas las listas menos a la especial
		for (Lista lista : listasTablero) {
			if (!lista.isEspecial()) {
				lista.configurarLimite(limite);
				repoListas.guardar(lista);
			}
		}

		// 7. Publicar un único evento de dominio
		LocalDateTime timestamp = LocalDateTime.now();
		eventBus.publicar(new LimiteTableroConfigurado(idTablero, idUsuario, timestamp, limite));
	}	
}