package es.um.pds.tarjetas.domain.model.tablero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private String nombre;						// Nombre del tablero
	private String url;							// URL que se genera del tablero
	private TableroId identificador;			// Identificador del tablero
	private Set<ListaId> listas;					// Aquí se guardan las listas del tablero
	private EstadoBloqueo estadoBloqueo;	// 
	
	// Constructor
	public Tablero(TableroId id, String nombre, UsuarioId usuarioCreador) {
		this.nombre = nombre;
		this.identificador = id;
		this.listas = new HashSet<>();
		this.estadoBloqueo = null;
	}
	
	// Getters y setters
	public String getNombre() {
		return this.nombre;
	}
	
	public void setNombre(String nuevoNombre, UsuarioId creador) throws Exception {
		
		this.nombre = nuevoNombre;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public TableroId getIdentificador() {
		return this.identificador;
	}
	
	public Set<ListaId> getListas() {
		return Collections.unmodifiableSet(listas);
	}
	
	public boolean isBloqueado() {
		return (this.estadoBloqueo != null);
	}
	
	// Funcionalidades
	public void anadirLista(ListaId lista, String nombre, UsuarioId user) throws Exception {
		if(this.isBloqueado()) {
			throw new Exception("No se pueden añadir listas porque el tablero está bloqueado.");
		}
		
		Lista nueva = new Lista(nombre, lista);
		this.listas.add(nueva);
	}
	
	public void bloquear(EstadoBloqueo motivo, UsuarioId user) throws Exception{
		if(this.isBloqueado()) {
			throw new Exception("El tablero ya está bloqueado");
		} else if(motivo == null) {
			throw new IllegalArgumentException("El motivo no puede ser nulo");
		} else {
			this.estadoBloqueo = motivo;
		}
	}
	
	public void desbloquear(UsuarioId user) throws Exception {
		if(!this.isBloqueado()) {
			throw new Exception("El tablero ya está desbloqueado");
		} else {
			this.estadoBloqueo = null;
		}
	}
	
	public void renombrarLista(ListaId lista, String nuevoNombre, UsuarioId user) throws Exception{
		
		Lista destino = this.listas.stream()
				.filter(l -> l.getId().equals(lista))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		destino.renombrar(nuevoNombre);
	}
	
	public void eliminarLista(ListaId lista, UsuarioId user) throws Exception {

		
		Lista eliminada = this.listas.stream()
				.filter(l -> l.getId().equals(lista))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		this.listas.remove(eliminada);
	}
	
	public void definirListaEspecial(ListaId lista, UsuarioId user) throws Exception {
		
		Lista seleccionada = this.listas.stream()
				.filter(l -> l.getId().equals(lista))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		seleccionada.hacerEspecial();
	}
	
	public void configurarLimiteLista(ListaId lista, int limite, UsuarioId user) throws Exception {
		
		Lista seleccionada = this.listas.stream()
				.filter(l -> l.getId().equals(lista))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		seleccionada.configurarLimite(limite);
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
