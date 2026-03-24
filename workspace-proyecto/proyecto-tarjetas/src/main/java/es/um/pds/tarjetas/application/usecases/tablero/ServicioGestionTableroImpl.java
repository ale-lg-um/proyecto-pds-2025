package es.um.pds.tarjetas.application.usecases.tablero;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import es.um.pds.tarjetas.domain.model.entryHistorial.EntryHistorialId;
import es.um.pds.tarjetas.application.usecases.historial.ServicioHistorialImpl;
import es.um.pds.tarjetas.domain.model.entryHistorial.EntryHistorial;
import es.um.pds.tarjetas.domain.model.lista.Lista;
import es.um.pds.tarjetas.domain.model.lista.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.EstadoBloqueo;
import es.um.pds.tarjetas.domain.model.tablero.Tablero;
import es.um.pds.tarjetas.domain.model.tablero.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.ContenidoTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.TarjetaId;
import es.um.pds.tarjetas.domain.model.usuario.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioGestionTablero;
import es.um.pds.tarjetas.domain.ports.input.ServicioHistorial;
import es.um.pds.tarjetas.domain.ports.input.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
import es.um.pds.tarjetas.domain.services.PoliticaTarjetas;

// TODO Eventos de dominio para las entries del historial
// TODO ¿Detalles relevantes para la entry del historial extraerlos aquí? ¿Timestamp dónde se genera? ¿Detalles?

@Service
public class ServicioGestionTableroImpl implements ServicioGestionTablero{
	// Inyectamos dependencias estrictas (patrón fachada)
	private final RepositorioTableros repoTableros;
	private final RepositorioListas repoListas;
	private final PoliticaTarjetas politicaTarjetas;
	
	// Constructor
	public ServicioGestionTableroImpl(RepositorioTableros repoTableros, RepositorioListas repoListas, PoliticaTarjetas politica) {
		this.repoTableros = repoTableros;
		this.repoListas = repoListas;
		this.politicaTarjetas = politica;
	}
	
	// Métodos heredados de la clase padre
	@Override
	public TableroId crearTablero(CrearTableroCmd cmd) throws Exception {	
		// Generar id del tablero
		TableroId nuevoId = TableroId.of(); // Valor de ejemplo que devuelve un Long, se puede cambiar
		
		// Generar URL del tablero
		String tokenUrl = UUID.randomUUID().toString();
		
		// Crear la entidad a partir del dominio (patrón creador)
		Tablero nuevo = Tablero.of(nuevoId, cmd.nombreTablero(), tokenUrl);
		
		// Guardar el tablero
		this.repoTableros.guardar(nuevo);
		
		// Devolver el id del tablero creado
		return nuevoId;
	}
	
	@Override
	public void renombrarTablero(TableroId tablero, String nombreNuevo, String emailUsuario) throws Exception {
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.renombrar(nombreNuevo);
		this.repoTableros.guardar(tab);
		
		// TODO Evento de dominio creación de la entrada del historial
	}
	
	@Override
	public void eliminarTablero(TableroId tablero, String emailUsuario) throws Exception {
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		this.repoTableros.eliminarPorId(tablero);
		
		// TODO Evento de dominio creación de la entrada del historial
	}
	
	@Override
	public void bloquearTablero(TableroId tablero, EstadoBloqueo espec, String emailUsuario) throws Exception {
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.bloquear(espec);
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public void desbloquearTablero(TableroId tablero, String emailUsuario) throws Exception {
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.desbloquear();
		this.repoTableros.guardar(tab);
	}
	
	// TODO
	@Override
	public ListaId crearLista(TableroId tablero, String nombre, String emailUsuario) throws Exception {
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		ListaId nuevaLista = ListaId.of();
		tab.anadirLista(nuevaLista);
		this.repoListas.guardar();
		return nuevaLista;
	}
	
	@Override
	public void renombrarLista(TableroId tablero, ListaId lista, String nombreNuevo, String emailUsuario) throws Exception {
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		Lista lis = this.repoListas.buscarPorId(lista)
				.orElseThrow(() -> new Exception("Lista no encontrada"));
		
		lis.renombrar(nombreNuevo); // Lo tengo que hacer llamando a Lista, que es la raíz del agregado
		this.repoListas.guardar(lis);
	}
	
	// TODO
	@Override
	public void eliminarLista(TableroId tablero, ListaId lista, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.eliminarLista(lista, user);
		this.repoTableros.guardar(tab);
	}
	
	/**
	 * Marca una lista en el tablero como especial. Solo puede haber una lista especial en el tablero
	 * La lista que se declare como especial no puede tener límite N configurado. Si lo tenía de antes, el límite
	 * N se suprimirá. Sí que puede tener prerrequisitos de haber pasado por otras listas
	 */
	@Override
	public void definirListaEspecial(TableroId tablero, ListaId lista, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.definirListaEspecial(lista, user);
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public void configurarLimiteLista(TableroId tablero, ListaId lista, int limite, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.configurarLimiteLista(lista, limite, user);
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public void configurarPrerrequisitosLista(TableroId tablero, ListaId lista, List<ListaId> prerrequisitos, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.configurarPrerrequisitosLista(lista, prerrequisitos, user);
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public TarjetaId crearTarjeta(TableroId tablero, ListaId lista, ContenidoTarjeta contenido, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		politicaTarjetas.validarCreacion(tab, lista);
		TarjetaId nueva = TarjetaId.of(System.currentTimeMillis());
		
		tab.anadirTarjetaALista(lista, nueva, contenido, user);
		repoTableros.guardar(tab);
		return nueva;
	}
	
	@Override
	public void editarTarjeta(TableroId tablero, TarjetaId tarjeta, ContenidoTarjeta nuevoCont, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.editarTarjeta(tarjeta, nuevoCont, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void eliminarTarjeta(TableroId tablero, TarjetaId tarjeta, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.eliminarTarjeta(tarjeta, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void moverTarjeta(TableroId tablero, TarjetaId tarjeta, ListaId lista, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		politicaTarjetas.validarMovimiento(tab, tarjeta, lista);
		tab.moverTarjeta(tarjeta, lista, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void completarTarjeta(TableroId tablero, TarjetaId tarjeta, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		politicaTarjetas.validarCompletar(tab, tarjeta);
		tab.completarTarjeta(tarjeta, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void addEtiquetaATarjeta(TableroId tablero, TarjetaId tarjeta, String nombre, String color, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.addEtiqueta(tarjeta, nombre, color, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void eliminarEtiquetaDeTarjeta(TableroId tablero, TarjetaId tarjeta, String nombre, String color, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.eliminarEtiqueta(tarjeta, nombre, color, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void modificarEtiquetaEnTarjeta(TableroId tablero, TarjetaId tarjeta, String nombreOld, String colorOld, String nombreNuevo, String colorNuevo, String emailUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(emailUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.modificarEtiqueta(tarjeta, nombreOld, colorOld, nombreNuevo, colorNuevo, user);
		repoTableros.guardar(tab);
	}
}
