package es.um.pds.tarjetas.application.usecases.tablero;

import java.util.List;

import es.um.pds.tarjetas.domain.model.tablero.EspecBloqueo;
import es.um.pds.tarjetas.domain.model.tablero.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.Tablero;
import es.um.pds.tarjetas.domain.model.tablero.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.ContenidoTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.TarjetaId;
import es.um.pds.tarjetas.domain.model.usuario.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.tablero.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.output.tablero.RepositorioTableros;
import es.um.pds.tarjetas.domain.services.PoliticaTarjetas;
import es.um.pds.tarjetas.domain.ports.input.tablero.ServicioGestionTablero;

public class ServicioGestionTableroImpl implements ServicioGestionTablero{
	// Inyectamos dependencias estrictas (patrón fachada)
	private final RepositorioTableros repoTableros;
	private final PoliticaTarjetas politicaTarjetas;
	
	// Constructor
	public ServicioGestionTableroImpl(RepositorioTableros repoTableros, PoliticaTarjetas politica) {
		this.repoTableros = repoTableros;
		this.politicaTarjetas = politica;
	}
	
	// Métodos heredados de la clase padre
	@Override
	public TableroId crearTablero(CrearTableroCmd cmd) throws Exception {
		// Traducir datos a los VOs del dominio
		UsuarioId creador = UsuarioId.of(cmd.emailCreador()); // Sacamos el email del creador del Record
		
		// Generar id del tablero
		TableroId nuevoId = TableroId.of(System.currentTimeMillis()); // Valor de ejemplo que devuelve un Long, se puede cambiar
		
		// Crear la entidad a partir del dominio (patrón creador)
		Tablero nuevo = new Tablero(nuevoId, cmd.nombreTablero(), creador);
		
		// Guardar el tablero
		this.repoTableros.guardar(nuevo);
		
		// Devolver el id del tablero creado
		return nuevoId;
	}
	
	@Override
	public void renombrarTablero(TableroId tablero, String nuevoNombre, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.setNombre(nuevoNombre, user);
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public void eliminarTablero(TableroId tablero, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		this.repoTableros.eliminarPorId(tablero);
	}
	
	@Override
	public void bloquearTablero(TableroId tablero, EspecBloqueo espec, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.bloquear(espec, user);
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public void desbloquearTablero(TableroId tablero, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.desbloquear(user);
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public ListaId crearLista(TableroId tablero, String nombre, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		ListaId nuevaLista = ListaId.of(System.currentTimeMillis());
		tab.anadirLista(nuevaLista, nombre, user);
		this.repoTableros.guardar(tab);
		return nuevaLista;
	}
	
	@Override
	public void renombrarLista(TableroId tablero, ListaId lista, String nuevoNombre, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.renombrarLista(lista, nuevoNombre, user); // Lo tengo que hacer llamando a Tablero, que es la raíz del agregado
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public void eliminarLista(TableroId tablero, ListaId lista, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.eliminarLista(lista, user);
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public void definirListaEspecial(TableroId tablero, ListaId lista, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.definirListaEspecial(lista, user);
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public void configurarLimiteLista(TableroId tablero, ListaId lista, int limite, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.configurarLimiteLista(lista, limite, user);
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public void configurarPrerrequisitosLista(TableroId tablero, ListaId lista, List<ListaId> prerrequisitos, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.configurarPrerrequisitosLista(lista, prerrequisitos, user);
		this.repoTableros.guardar(tab);
	}
	
	@Override
	public TarjetaId crearTarjeta(TableroId tablero, ListaId lista, ContenidoTarjeta contenido, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		politicaTarjetas.validarCreacion(tab, lista);
		TarjetaId nueva = TarjetaId.of(System.currentTimeMillis());
		
		tab.anadirTarjetaALista(lista, nueva, contenido, user);
		repoTableros.guardar(tab);
		return nueva;
	}
	
	@Override
	public void editarTarjeta(TableroId tablero, TarjetaId tarjeta, ContenidoTarjeta nuevoCont, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.editarTarjeta(tarjeta, nuevoCont, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void eliminarTarjeta(TableroId tablero, TarjetaId tarjeta, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.eliminarTarjeta(tarjeta, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void moverTarjeta(TableroId tablero, TarjetaId tarjeta, ListaId lista, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		politicaTarjetas.validarMovimiento(tab, tarjeta, lista);
		tab.moverTarjeta(tarjeta, lista, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void completarTarjeta(TableroId tablero, TarjetaId tarjeta, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		politicaTarjetas.validarCompletar(tab, tarjeta);
		tab.completarTarjeta(tarjeta, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void addEtiquetaATarjeta(TableroId tablero, TarjetaId tarjeta, String nombre, String color, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.addEtiqueta(tarjeta, nombre, color, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void eliminarEtiquetaDeTarjeta(TableroId tablero, TarjetaId tarjeta, String nombre, String color, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.eliminarEtiqueta(tarjeta, nombre, color, user);
		repoTableros.guardar(tab);
	}
	
	@Override
	public void modificarEtiquetaEnTarjeta(TableroId tablero, TarjetaId tarjeta, String nombreOld, String colorOld, String nombreNuevo, String colorNuevo, String correoUsuario) throws Exception {
		UsuarioId user = UsuarioId.of(correoUsuario);
		Tablero tab = this.repoTableros.buscarPorId(tablero)
				.orElseThrow(() -> new Exception("Tablero no encontrado"));
		
		tab.modificarEtiqueta(tarjeta, nombreOld, colorOld, nombreNuevo, colorNuevo, user);
		repoTableros.guardar(tab);
	}
}
