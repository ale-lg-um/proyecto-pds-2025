package es.um.pds.tarjetas.domain.model.tablero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import es.um.pds.tarjetas.domain.exceptions.TableroInvalidoException;
import es.um.pds.tarjetas.domain.exceptions.TarjetaInvalidaException;
import es.um.pds.tarjetas.domain.model.lista.Lista;
import es.um.pds.tarjetas.domain.model.lista.ListaId;
import es.um.pds.tarjetas.domain.model.tarjeta.ContenidoTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.Etiqueta;
import es.um.pds.tarjetas.domain.model.tarjeta.Tarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.TarjetaId;
import es.um.pds.tarjetas.domain.model.usuario.UsuarioId;

//@Entity
public class Tablero {
	// Atributos
	private TableroId identificador;			// Identificador del tablero
	private String nombre;						// Nombre del tablero
	private String tokenUrl;					// Token de la URL que se genera del tablero
	private Set<ListaId> listas;				// Aquí se guardan las listas del tablero
	private EstadoBloqueo estadoBloqueo;		// 
	
	// Constructor
	private Tablero(TableroId identificador, String nombre, String tokenUrl) {
		this.identificador = identificador;
		this.nombre = nombre;
		this.tokenUrl = tokenUrl;
		this.listas = new HashSet<>();
		this.estadoBloqueo = null;	// Empieza sin estar bloqueado
	}
	
	public static Tablero of(TableroId identificador, String nombre, String tokenUrl) throws TableroInvalidoException {
		if (identificador == null) {
			throw new TableroInvalidoException("El tablero debe tener un identificador");
		}
		
		if (nombre == null) {
			throw new TableroInvalidoException("El tablero debe tener un nombre");
		}
		
		if (tokenUrl == null) {
			throw new TableroInvalidoException("El tablero debe tener un token para su URL");
		}
	}
	
	// Getters
	
	public TableroId getIdentificador() {
		return this.identificador;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public String getTokenUrl() {
		return this.tokenUrl;
	}
	
	public Set<ListaId> getListas() {
		return Collections.unmodifiableSet(listas);
	}
	
	public boolean isBloqueado() {
		return (this.estadoBloqueo != null);
	}
	
	// Funcionalidades
	public void anadirLista(ListaId nuevaLista) {
		if (this.isBloqueado()) {
			throw new IllegalStateException("No se pueden añadir listas porque el tablero está bloqueado");
		}
		
		if (nuevaLista == null) {
			throw new IllegalArgumentException("La lista que se desea añadir no puede ser nula");
		}
		
		this.listas.add(nuevaLista);
	}
	
	public void bloquear(EstadoBloqueo bloqueo) {
		if(this.isBloqueado()) {
			throw new IllegalStateException("El tablero ya está bloqueado");
		} else if(bloqueo == null) {
			throw new IllegalArgumentException("El bloqueo no puede ser nulo");
		} else {
			this.estadoBloqueo = bloqueo;
		}
	}
	
	public void desbloquear() {
		if (!this.isBloqueado()) {
			throw new IllegalStateException("El tablero ya está desbloqueado");
		} else {
			this.estadoBloqueo = null;
		}
	}
	
	
	public void eliminarLista(ListaId lista) {	
		if (lista == null) {
			throw new IllegalArgumentException("La lista que se desea eliminar no puede ser nula");	
		}
		
		if (!listas.contains(lista)) {
			throw new IllegalArgumentException("La lista que se desea eliminar no existe");
		}
		
		this.listas.remove(lista);
	}
	

	
	public void configurarPrerrequisitosLista(ListaId lista, List<ListaId> prerrequisitos, UsuarioId user) throws Exception {

		
		Lista seleccionada = this.listas.stream()
				.filter(l -> l.getId().equals(lista))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		seleccionada.configurarPrerrequisitos(prerrequisitos);
	}
	
	public void anadirTarjetaALista (ListaId lista, TarjetaId tarjeta, ContenidoTarjeta contenido, UsuarioId user) throws Exception {

		
		Lista seleccionada = this.listas.stream()
				.filter(l -> l.getId().equals(lista))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		Tarjeta nueva = Tarjeta.of(tarjeta, contenido.getTipo(), contenido.getElementos());
		seleccionada.anadirTarjeta(nueva);
	}
	
	public void moverTarjeta(TarjetaId tarjeta, ListaId destino, UsuarioId user) throws Exception {
		
		Lista dest = this.listas.stream()
				.filter(l -> l.getId().equals(destino))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		Lista origen = this.listas.stream()
				.filter(l -> l.getListaTarjetas().stream()
						.anyMatch(t -> t.getIdentificador().equals(tarjeta)))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No existen listas de tarjetas con ese ID"));
		
		Tarjeta tarj = origen.getListaTarjetas().stream()
				.filter(t -> t.getIdentificador().equals(tarjeta))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No se ha encontrado la tarjeta"));
		
		origen.quitarTarjeta(tarj);
		dest.anadirTarjeta(tarj);
	}
	
	public void editarTarjeta(TarjetaId tarjeta, ContenidoTarjeta nuevoCont, UsuarioId user) throws Exception {
		
		Lista origen = this.listas.stream()
				.filter(l -> l.getListaTarjetas().stream()
						.anyMatch(t -> t.getIdentificador().equals(tarjeta)))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No existen listas de tarjetas con ese ID"));
		
		Tarjeta tarj = origen.getListaTarjetas().stream()
				.filter(t -> t.getIdentificador().equals(tarjeta))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No se ha encontrado la tarjeta"));
		
		origen.quitarTarjeta(tarj);
		tarj.setContenido(nuevoCont);
		origen.anadirTarjeta(tarj);
	}
	
	public void eliminarTarjeta(TarjetaId tarjeta, UsuarioId user) throws Exception {

		
		Lista origen = this.listas.stream()
				.filter(l -> l.getListaTarjetas().stream()
						.anyMatch(t -> t.getIdentificador().equals(tarjeta)))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No existen listas de tarjetas con ese ID"));
		
		Tarjeta tarj = origen.getListaTarjetas().stream()
				.filter(t -> t.getIdentificador().equals(tarjeta))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No se ha encontrado la tarjeta"));
		
		origen.quitarTarjeta(tarj);
	}
	
	public void completarTarjeta(TarjetaId tarjeta, UsuarioId user) throws Exception {

		
		Lista origen = this.listas.stream()
				.filter(l -> l.getListaTarjetas().stream()
						.anyMatch(t -> t.getIdentificador().equals(tarjeta)))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No existen listas de tarjetas con ese ID"));
		
		Tarjeta tarj = origen.getListaTarjetas().stream()
				.filter(t -> t.getIdentificador().equals(tarjeta))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No se ha encontrado la tarjeta"));
		
		Lista dest = this.listas.stream()
				.filter(l -> l.isEspecial() == true)
				.findFirst()
				.orElseThrow(() -> new Exception("El tablero no contiene listas especiales."));
		
		origen.quitarTarjeta(tarj);
		tarj.completar();
		dest.anadirTarjeta(tarj);
	}
	
	public void addEtiqueta(TarjetaId tarjeta, String nombre, String color, UsuarioId user) throws Exception {

		
		Lista origen = this.listas.stream()
				.filter(l -> l.getListaTarjetas().stream()
						.anyMatch(t -> t.getIdentificador().equals(tarjeta)))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No existen listas de tarjetas con ese ID"));
		
		Tarjeta tarj = origen.getListaTarjetas().stream()
				.filter(t -> t.getIdentificador().equals(tarjeta))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No se ha encontrado la tarjeta"));
		
		Etiqueta etiqueta = new Etiqueta(nombre, color);
		tarj.anadirEtiqueta(etiqueta);
	}
	
	public void eliminarEtiqueta(TarjetaId tarjeta, String nombre, String color, UsuarioId user) throws Exception {

		
		Lista origen = this.listas.stream()
				.filter(l -> l.getListaTarjetas().stream()
						.anyMatch(t -> t.getIdentificador().equals(tarjeta)))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No existen listas de tarjetas con ese ID"));
		
		Tarjeta tarj = origen.getListaTarjetas().stream()
				.filter(t -> t.getIdentificador().equals(tarjeta))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No se ha encontrado la tarjeta"));
		
		Etiqueta eliminada = tarj.getEtiquetas().stream()
				.filter(e -> e.nombre().equals(nombre) && e.color().equals(color))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La tarjeta no tiene esa etiqueta"));
		
		tarj.eliminarEtiqueta(eliminada);
	}
	
	public void modificarEtiqueta(TarjetaId tarjeta, String nombreOld, String colorOld, String nombreNuevo, String colorNuevo, UsuarioId user) throws Exception {

		
		Lista origen = this.listas.stream()
				.filter(l -> l.getListaTarjetas().stream()
						.anyMatch(t -> t.getIdentificador().equals(tarjeta)))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No existen listas de tarjetas con ese ID"));
		
		Tarjeta tarj = origen.getListaTarjetas().stream()
				.filter(t -> t.getIdentificador().equals(tarjeta))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No se ha encontrado la tarjeta"));
		
		Etiqueta modificada = tarj.getEtiquetas().stream()
				.filter(e -> e.nombre().equals(nombreOld) && e.color().equals(colorOld))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La tarjeta no tiene esa etiqueta"));
		
		tarj.eliminarEtiqueta(modificada);
		
		Etiqueta modificacion = new Etiqueta(nombreNuevo, colorNuevo);
		
		tarj.anadirEtiqueta(modificacion);
	}
}
